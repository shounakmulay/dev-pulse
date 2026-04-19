package dev.shounakmulay.feature.devtools.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.devtools.ui.DeveloperToolsScreen

fun EntryProviderScope<Screen>.developerToolsScreenEntry(navigator: Navigator) {
    entry<Screen.DeveloperTools> {
        DeveloperToolsScreen(navigator)
    }
}