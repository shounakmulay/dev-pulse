package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.SettingsListItem
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SettingsScreenState(
    val isLoading: Boolean = false,
    val isBlackMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val settingsList: List<SettingsListItem>
) : ScreenState {
    val canToggleBlackMode: Boolean
        get() = themeMode != ThemeMode.LIGHT

    fun canToggleBlackMode(isDarkTheme: Boolean): Boolean =
        when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isDarkTheme
        }
}
