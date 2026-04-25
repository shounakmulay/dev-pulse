package dev.shounakmulay.feature.settings.screens.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.feature.settings.screens.settings.ui.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.settingsScreen(navigator: Navigator) {
    entry<Screen.Settings>(
        metadata = ExpandableListDetailSceneStrategy.listPane()
    ) {
        SettingsScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
