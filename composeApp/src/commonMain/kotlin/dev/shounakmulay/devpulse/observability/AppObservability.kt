package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.core.domain.settings.privacy.TelemetryPrivacyCoordinator
import dev.shounakmulay.devpulse.core.logging.DPLog
import dev.shounakmulay.devpulse.core.remoteconfig.ConfigActivation
import dev.shounakmulay.devpulse.core.remoteconfig.RemoteConfig
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryCollectionPolicy
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

internal class AppObservability(
    private val remoteConfig: RemoteConfig,
    private val telemetry: TelemetryControl,
    private val privacy: TelemetryPrivacyCoordinator,
    private val providers: ObservabilityProviders,
) {
    private val started = MutableStateFlow(false)
    private val refreshRequests = Channel<Unit>(Channel.CONFLATED)

    fun start(scope: CoroutineScope) {
        if (!started.compareAndSet(false, true)) return
        scope.launch {
            report(remoteConfig.initialize(), "Configuration cache unavailable")
            report(remoteConfig.activate(ConfigActivation.APP_START), "Configuration activation failed")
            applyPolicy()
            report(privacy.initialize(), "Privacy settings unavailable")
            launch {
                combine(
                    remoteConfig.observe(AppConfigKeys.disableTelemetry),
                    remoteConfig.observe(AppConfigKeys.traceSampleRate),
                ) { _, _ -> policyFromSnapshot() }
                    .distinctUntilChanged()
                    .collect { policy ->
                        val result = telemetry.updatePolicy(policy)
                        if (!result.applied || result.failedProviders.isNotEmpty()) {
                            DPLog.w("Observability") { "Collection controls could not finish updating" }
                        }
                    }
            }
            launch {
                var lastAttempt: TimeSource.Monotonic.ValueTimeMark? = null
                for (request in refreshRequests) {
                    if (lastAttempt?.elapsedNow()?.let { it < 15.minutes } == true) continue
                    lastAttempt = TimeSource.Monotonic.markNow()
                    coroutineScope {
                        providers.config.forEach { provider -> launch { refresh(provider.namespace) } }
                    }
                }
            }
            foreground()
        }
    }

    fun foreground() {
        refreshRequests.trySend(Unit)
    }

    private suspend fun refresh(namespace: String) {
        repeat(3) { attempt ->
            val result = withTimeoutOrNull(10.seconds) { remoteConfig.fetch(namespace) }
            if (result?.isSuccess == true) {
                report(remoteConfig.activate(ConfigActivation.IMMEDIATE), "Configuration activation failed")
                return
            }
            if (attempt < 2) delay(if (attempt == 0) 1.seconds else 5.seconds)
        }
        DPLog.w("Observability") { "Configuration refresh unavailable" }
    }

    private suspend fun applyPolicy() {
        val result = telemetry.updatePolicy(policyFromSnapshot())
        if (!result.applied || result.failedProviders.isNotEmpty()) {
            DPLog.w("Observability") { "Collection controls could not finish updating" }
        }
    }

    private fun report(result: Result<Unit>, message: String) {
        result.onFailure { DPLog.w("Observability") { message } }
    }

    private fun policyFromSnapshot(): TelemetryCollectionPolicy {
        val snapshot = remoteConfig.snapshot()
        return TelemetryCollectionPolicy(
            enabled = !snapshot.evaluate(AppConfigKeys.disableTelemetry).value,
            traceSampleRate = snapshot.evaluate(AppConfigKeys.traceSampleRate).value,
        )
    }
}
