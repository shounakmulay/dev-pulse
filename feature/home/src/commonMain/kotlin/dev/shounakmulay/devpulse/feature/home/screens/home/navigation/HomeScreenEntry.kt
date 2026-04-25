package dev.shounakmulay.devpulse.feature.home.screens.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.home.screens.home.ui.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.homeScreen(navigator: Navigator) {
    entry<Screen.Tabs.Home> {
        HomeScreen(
            viewModel = koinViewModel(),
            navigator = navigator
        )
    }
}