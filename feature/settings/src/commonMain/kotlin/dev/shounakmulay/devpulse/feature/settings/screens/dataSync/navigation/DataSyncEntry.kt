package dev.shounakmulay.devpulse.feature.settings.screens.dataSync.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.dataSync.ui.DataSyncScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.dataSyncEntry(navigator: Navigator) {
    entry<Screen.DeveloperTools.DataAndSync> {
        DataSyncScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
