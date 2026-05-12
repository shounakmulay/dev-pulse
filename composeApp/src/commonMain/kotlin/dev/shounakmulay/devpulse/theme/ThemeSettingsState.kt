package dev.shounakmulay.devpulse.theme

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.ThemeMode

@Immutable
data class ThemeSettingsState(
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val isBlackMode: Boolean = false,
    val darkTheme: Boolean = false
)
