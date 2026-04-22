package dev.shounakmulay.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.settings.screens.settings.navigation.settingsScreen

fun EntryProviderScope<Screen>.settingsFeatureEntries(navigator: Navigator) {
    settingsScreen(navigator)
}
