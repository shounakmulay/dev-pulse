package dev.shounakmulay.feature.devtools.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.feature.devtools.ui.DesignSystemBoardScreen

fun EntryProviderScope<Screen>.designSystemBoard(navigator: Navigator) {
    entry<Screen.DeveloperTools.DesignSystemBoard>(
        metadata = ExpandableListDetailSceneStrategy.detailPane()
    ) {
        DesignSystemBoardScreen(navigator)
    }
}