package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class SettingsScreenState(
    val feedPostListItemVariant: FeedsPostListItemVariant = FeedsPostListItemVariant.DEFAULT,
    val isBlackMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val syncInBackground: Boolean = true,
    @Transient val crashReporting: Boolean = false,
    @Transient val performanceMetrics: Boolean = false,
    @Transient val isTelemetryConsentLoaded: Boolean = false,
    @Transient val isSavingTelemetryConsent: Boolean = false,
    @Transient val telemetryConsentError: Boolean = false,
) : ScreenState {
    fun canToggleBlackMode(isDarkTheme: Boolean): Boolean = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isDarkTheme
    }
}
