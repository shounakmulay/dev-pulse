package dev.shounakmulay.devpulse.theme

import dev.shounakmulay.devpulse.core.domain.models.ThemeMode

fun ThemeMode.resolveDarkTheme(isSystemInDarkTheme: Boolean): Boolean =
    when (this) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme
    }
