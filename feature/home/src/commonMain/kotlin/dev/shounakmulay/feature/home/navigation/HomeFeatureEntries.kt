package dev.shounakmulay.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.feature.home.screens.home.navigation.homeScreen

fun EntryProviderScope<Screen>.homeFeatureEntries(navigator: Navigator) {
    homeScreen(navigator)
}