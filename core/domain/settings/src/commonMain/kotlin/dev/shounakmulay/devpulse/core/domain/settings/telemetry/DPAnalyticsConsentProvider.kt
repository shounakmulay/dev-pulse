package dev.shounakmulay.devpulse.core.domain.settings.telemetry

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.common.startup.StartupPriority
import dev.shounakmulay.devpulse.core.data.settings.TelemetryConsentRepository
import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.logging.consent.applyTelemetryConsent
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class DPAnalyticsConsentProvider(
    private val repository: TelemetryConsentRepository,
) : AnalyticsConsentProvider,
    StartupAsyncInitialiser {
    private val mutex = Mutex()
    private val consent = MutableStateFlow<DPAnalyticsConsent?>(null)
    override val priority = StartupPriority.HIGHEST

    override fun getConsent(): DPAnalyticsConsent? = consent.value
    override fun observeConsent() = consent.asStateFlow()

    override suspend fun initialise() {
        mutex.withLock {
            updateConsent(repository.observeConsent().first())
        }
    }

    suspend fun setConsent(value: DPAnalyticsConsent) {
        mutex.withLock {
            currentCoroutineContext().ensureActive()
            withContext(NonCancellable) {
                repository.setConsent(value)
                updateConsent(value)
            }
            currentCoroutineContext().ensureActive()
        }
    }

    private fun updateConsent(value: DPAnalyticsConsent?) {
        val effectiveConsent = value ?: DPAnalyticsConsent()
        consent.value = effectiveConsent
        applyTelemetryConsent(effectiveConsent)
    }
}
