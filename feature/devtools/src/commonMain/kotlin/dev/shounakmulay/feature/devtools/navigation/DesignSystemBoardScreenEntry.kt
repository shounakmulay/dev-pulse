package dev.shounakmulay.feature.devtools.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.designsystem.components.devtools.DesignSystemBoard
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen

fun EntryProviderScope<Screen>.designSystemBoard(navigator: Navigator) {
    entry<Screen.DeveloperTools.DesignSystemBoard> {
        DesignSystemBoard()
    }
}