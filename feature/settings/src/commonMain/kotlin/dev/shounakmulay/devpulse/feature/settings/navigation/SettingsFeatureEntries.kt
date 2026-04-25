package dev.shounakmulay.devpulse.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.settings.navigation.settingsScreen

fun EntryProviderScope<Screen>.settingsFeatureEntries(navigator: Navigator) {
    settingsScreen(navigator)
}
