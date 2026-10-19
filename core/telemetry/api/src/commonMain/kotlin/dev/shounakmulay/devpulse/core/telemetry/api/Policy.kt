package dev.shounakmulay.devpulse.core.telemetry.api

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow

enum class TelemetryPurpose { Analytics, Diagnostics }

data class TelemetryConsent(
    val analytics: Boolean = false,
    val diagnostics: Boolean = false,
    val policyVersion: Int = 1,
) {
    init { require(policyVersion > 0) }
    fun allows(purpose: TelemetryPurpose): Boolean = when (purpose) {
        TelemetryPurpose.Analytics -> analytics
        TelemetryPurpose.Diagnostics -> diagnostics
    }
}

data class AppReleaseContext(
    val appId: String,
    val platform: String,
    val appVersion: String,
    val buildNumber: String,
    val environment: String,
    val channel: String,
    val commitId: String? = null,
) {
    init {
        require(listOf(appId, platform, appVersion, buildNumber, environment, channel).all {
            it.isNotBlank() && it.length <= 160 && it.none(Char::isISOControl)
        })
        require(commitId == null || commitId.length <= 160 && commitId.none(Char::isISOControl))
    }
}

data class TelemetryCollectionPolicy(val enabled: Boolean = true, val traceSampleRate: Double = 1.0) {
    init { require(traceSampleRate.isFinite() && traceSampleRate in 0.0..1.0) }
}

enum class RuntimeStatus { LocalReady, Ready, Closing, Closed }
enum class ProviderStatus { Disabled, Starting, Ready, Degraded, Closed }

data class ProviderHealth(
    val id: String,
    val status: ProviderStatus,
    val queued: Int,
    val accepted: Long,
    val failures: Long,
    val dropped: Long,
    val capabilities: PersistentList<TelemetryCapability>,
)

data class TelemetryHealth(
    val providers: PersistentList<ProviderHealth> = persistentListOf(),
    val denied: Long = 0,
    val invalid: Long = 0,
    val queueDrops: Long = 0,
    val expired: Long = 0,
    val stale: Long = 0,
)

data class TelemetryState(
    val status: RuntimeStatus = RuntimeStatus.LocalReady,
    val consent: TelemetryConsent = TelemetryConsent(),
    val analyticsGeneration: Long = 0,
    val diagnosticsGeneration: Long = 0,
    val collectionPolicy: TelemetryCollectionPolicy = TelemetryCollectionPolicy(),
)

data class ConsentApplyReport(val applied: Boolean, val failedProviders: Set<String>)

interface TelemetryControl {
    val state: StateFlow<TelemetryState>
    suspend fun applyConsent(consent: TelemetryConsent): ConsentApplyReport
    suspend fun pause(purpose: TelemetryPurpose): ConsentApplyReport
    suspend fun updatePolicy(policy: TelemetryCollectionPolicy): ConsentApplyReport
}

interface TelemetryInspector { fun snapshot(): TelemetryHealth }
