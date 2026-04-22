package dev.shounakmulay.feature.devtools.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.devtools.ui.DesignSystemBoardScreen

fun EntryProviderScope<Screen>.designSystemBoard(navigator: Navigator) {
    entry<Screen.DeveloperTools.DesignSystemBoard> {
        DesignSystemBoardScreen(navigator)
    }
}