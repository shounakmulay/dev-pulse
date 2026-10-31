package dev.shounakmulay.devpulse.core.domain.settings.telemetry

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.settings.TelemetryConsentRepository
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class TelemetryConsentUseCaseTest {
    @Test
    fun `Given no saved choices When initialising Then only crash reporting is enabled`(): Unit = runBlocking {
        val provider = DPAnalyticsConsentProvider(ConsentRepository())
        assertEquals(null, provider.getConsent())
        provider.initialise()
        assertEquals(DPAnalyticsConsent(crashReporting = true, usageAnalytics = false, performanceMetrics = false), provider.getConsent())
    }

    @Test
    fun `Given a saved crash opt out When initialising Then the explicit choice is preserved`(): Unit = runBlocking {
        val repository = ConsentRepository().apply {
            consent.value = DPAnalyticsConsent(crashReporting = false, usageAnalytics = true, performanceMetrics = true)
        }
        val provider = DPAnalyticsConsentProvider(repository)
        provider.initialise()
        assertEquals(DPAnalyticsConsent(crashReporting = false, usageAnalytics = true, performanceMetrics = true), provider.getConsent())
    }

    @Test
    fun `Given no answer When declining and granting Then refusal remains distinct from unanswered consent`(): Unit = runBlocking {
        val repository = ConsentRepository()
        val observe = ObserveTelemetryConsentUseCase(repository, dispatchers)
        val set = SetTelemetryConsentUseCase(dispatchers, DPAnalyticsConsentProvider(repository))
        assertEquals(null, observe().first().getOrThrow())
        set(DPAnalyticsConsent(crashReporting = false, usageAnalytics = false, performanceMetrics = false)).getOrThrow()
        assertEquals(DPAnalyticsConsent(crashReporting = false, usageAnalytics = false, performanceMetrics = false), observe().first().getOrThrow())
        set(DPAnalyticsConsent(crashReporting = true, usageAnalytics = true, performanceMetrics = true)).getOrThrow()
        assertEquals(DPAnalyticsConsent(crashReporting = true, usageAnalytics = true, performanceMetrics = true), observe().first().getOrThrow())
    }

    @Test
    fun `Given a write failure When saving Then the failure is returned and consent stays unanswered`(): Unit = runBlocking {
        val failure = StorageFailure("storage unavailable")
        val repository = ConsentRepository().apply { writeFailure = failure }
        val set = SetTelemetryConsentUseCase(dispatchers, DPAnalyticsConsentProvider(repository))
        assertSame(failure, set(DPAnalyticsConsent(crashReporting = true, usageAnalytics = true, performanceMetrics = true)).exceptionOrNull())
        assertEquals(null, repository.consent.value)
    }

    @Test
    fun `Given write cancellation When saving Then cancellation propagates`(): Unit = runBlocking {
        val repository = ConsentRepository().apply { writeFailure = CancellationException("cancelled") }
        val set = SetTelemetryConsentUseCase(dispatchers, DPAnalyticsConsentProvider(repository))
        assertFailsWith<CancellationException> { set(DPAnalyticsConsent(crashReporting = true, usageAnalytics = true, performanceMetrics = true)) }
        assertEquals(null, repository.consent.value)
    }

    @Test
    fun `Given a read failure When observing Then the original failure is returned`(): Unit = runBlocking {
        val failure = IllegalStateException("read failed")
        val repository = ConsentRepository().apply { readFailure = failure }
        val observe = ObserveTelemetryConsentUseCase(repository, dispatchers)
        assertSame(failure, observe().first().exceptionOrNull())
    }

    @Test
    fun `Given flow construction failure When observing Then the original failure is returned`(): Unit = runBlocking {
        val failure = IllegalStateException("observation unavailable")
        val repository = ConsentRepository().apply { constructionFailure = failure }
        val observe = ObserveTelemetryConsentUseCase(repository, dispatchers)
        assertSame(failure, observe().first().exceptionOrNull())
    }

    @Test
    fun `Given read cancellation When observing Then cancellation propagates`(): Unit = runBlocking {
        val repository = ConsentRepository().apply { readFailure = CancellationException("cancelled") }
        val observe = ObserveTelemetryConsentUseCase(repository, dispatchers)
        assertFailsWith<CancellationException> { observe().first() }
    }

    private class StorageFailure(val detail: String) : Exception(detail)

    private class ConsentRepository : TelemetryConsentRepository {
        val consent = MutableStateFlow<DPAnalyticsConsent?>(null)
        var writeFailure: Exception? = null
        var readFailure: Exception? = null
        var constructionFailure: Exception? = null

        override fun observeConsent(): Flow<DPAnalyticsConsent?> {
            constructionFailure?.let { throw it }
            return flow {
                readFailure?.let { throw it }
                consent.collect { emit(it) }
            }
        }

        override suspend fun setConsent(consent: DPAnalyticsConsent) {
            writeFailure?.let { throw it }
            this.consent.value = consent
        }
    }

    private val dispatchers = object : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }
}
