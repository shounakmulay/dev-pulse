package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.ThemeSingleChoiceOptions

fun ThemeSingleChoiceOptions.toThemeMode(): ThemeMode =
    when (this) {
        ThemeSingleChoiceOptions.LIGHT -> ThemeMode.LIGHT
        ThemeSingleChoiceOptions.DARK -> ThemeMode.DARK
        ThemeSingleChoiceOptions.SYSTEM -> ThemeMode.SYSTEM
    }

fun ThemeMode.toThemeSingleChoiceOption(): ThemeSingleChoiceOptions =
    when (this) {
        ThemeMode.LIGHT -> ThemeSingleChoiceOptions.LIGHT
        ThemeMode.DARK -> ThemeSingleChoiceOptions.DARK
        ThemeMode.SYSTEM -> ThemeSingleChoiceOptions.SYSTEM
    }
