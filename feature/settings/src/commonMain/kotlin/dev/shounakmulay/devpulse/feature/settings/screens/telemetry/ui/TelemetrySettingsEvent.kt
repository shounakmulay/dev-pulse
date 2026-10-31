package dev.shounakmulay.devpulse.feature.settings.screens.telemetry.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface TelemetrySettingsEvent : ScreenEvent {
    data class SetCrashReporting(val enabled: Boolean) : TelemetrySettingsEvent
    data class SetAnalytics(val enabled: Boolean) : TelemetrySettingsEvent
    data object Retry : TelemetrySettingsEvent
}
