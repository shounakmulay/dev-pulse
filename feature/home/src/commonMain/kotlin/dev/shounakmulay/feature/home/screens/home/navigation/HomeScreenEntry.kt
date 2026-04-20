package dev.shounakmulay.feature.home.screens.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.home.screens.home.ui.HomeScreen

fun EntryProviderScope<Screen>.homeScreen(navigator: Navigator) {
    entry<Screen.Tabs.Home> {
        HomeScreen()
    }
}