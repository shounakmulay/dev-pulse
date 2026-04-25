package dev.shounakmulay.devpulse.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.home.screens.home.navigation.homeScreen

fun EntryProviderScope<Screen>.homeFeatureEntries(navigator: Navigator) {
    homeScreen(navigator)
}