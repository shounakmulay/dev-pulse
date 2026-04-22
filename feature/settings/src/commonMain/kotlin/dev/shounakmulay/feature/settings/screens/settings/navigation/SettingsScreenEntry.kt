package dev.shounakmulay.feature.settings.screens.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.settings.screens.settings.ui.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.settingsScreen(navigator: Navigator) {
    entry<Screen.Settings> {
        SettingsScreen(viewModel = koinViewModel())
    }
}
