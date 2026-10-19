package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ProviderJvmFailureTest {
    private val release = AppReleaseContext("test", "jvm", "1", "1", "test", "test")
    private val event = object : AnalyticsEvent {
        override val definition = EventDefinition("test.event", 1, "test")
        override val attributes = SafeAttributes.Empty
    }

    @Test
    fun `Given missing SDK native library When delivering again Then linkage failure stays inside provider boundary`() = runTest {
        val recording = RecordingTelemetryProvider()
        var fail = true
        val provider = object : TelemetryProvider by recording {
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome {
                if (fail) { fail = false; throw UnsatisfiedLinkError("missing native SDK") }
                return recording.deliver(record)
            }
        }
        val runtime = TelemetryRuntime(release, listOf(provider), StandardTestDispatcher(testScheduler))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runCurrent()
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1L, runtime.snapshot().providers.single().failures)
        assertEquals(2L, recording.records().single().context.sequence)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given SDK reports process fatal VM error When initializing Then fatal error is propagated`() = runTest {
        val fatal = OutOfMemoryError("simulated VM failure")
        val provider = object : TelemetryProvider by RecordingTelemetryProvider() {
            override suspend fun initialize(release: AppReleaseContext, policy: ProviderPolicy) { throw fatal }
        }
        val runtime = TelemetryRuntime(release, listOf(provider), StandardTestDispatcher(testScheduler))
        var caught: OutOfMemoryError? = null
        try { runtime.applyConsent(TelemetryConsent(analytics = true)) } catch (failure: OutOfMemoryError) { caught = failure }
        assertEquals("simulated VM failure", assertIs<OutOfMemoryError>(caught).message)
        assertTrue(runtime.close(1.seconds).completed)
    }
}
