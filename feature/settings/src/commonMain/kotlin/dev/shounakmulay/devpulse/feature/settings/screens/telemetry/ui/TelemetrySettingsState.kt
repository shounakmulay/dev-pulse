package dev.shounakmulay.devpulse.feature.settings.screens.telemetry.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class TelemetrySettingsState(
    @Transient val consent: DPAnalyticsConsent = DPAnalyticsConsent(),
    @Transient val loaded: Boolean = false,
    @Transient val saving: Boolean = false,
    @Transient val error: Boolean = false,
) : ScreenState
