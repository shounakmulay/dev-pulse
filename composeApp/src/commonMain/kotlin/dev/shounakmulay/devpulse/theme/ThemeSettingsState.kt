package dev.shounakmulay.devpulse.theme

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode

@Immutable
data class ThemeSettingsState(
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val isBlackMode: Boolean = false,
    val isSystemInDarkTheme: Boolean = false,
) {
    fun isDarkMode(): Boolean {
        return when (themeMode) {
            ThemeMode.SYSTEM if isSystemInDarkTheme -> true
            ThemeMode.DARK -> true
            else -> false
        }
    }
}
