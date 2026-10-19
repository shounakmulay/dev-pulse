package dev.shounakmulay.devpulse.core.domain.settings.privacy

import kotlinx.serialization.Serializable

@Serializable
enum class TelemetryPrivacyStatus {
    Loading,
    Ready,
    Saving,
    LoadFailed,
    SaveFailed,
    ApplyFailed,
}

@Serializable
data class TelemetryPrivacyState(
    val analytics: Boolean = false,
    val diagnostics: Boolean = false,
    val status: TelemetryPrivacyStatus = TelemetryPrivacyStatus.Loading,
) {
    val canChangeConsent: Boolean get() = status == TelemetryPrivacyStatus.Ready
    val canWithdrawConsent: Boolean get() = when (status) {
        TelemetryPrivacyStatus.Ready,
        TelemetryPrivacyStatus.SaveFailed,
        TelemetryPrivacyStatus.ApplyFailed -> true
        else -> false
    }
}
