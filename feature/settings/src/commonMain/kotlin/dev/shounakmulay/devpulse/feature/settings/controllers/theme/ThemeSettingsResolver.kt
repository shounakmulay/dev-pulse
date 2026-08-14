package dev.shounakmulay.devpulse.feature.settings.controllers.theme

import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode

fun ThemeMode.resolveDarkTheme(isSystemInDarkTheme: Boolean): Boolean =
    when (this) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme
    }
