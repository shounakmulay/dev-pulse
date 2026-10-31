package dev.shounakmulay.devpulse.feature.settings.screens.telemetry.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.telemetry.ui.TelemetrySettingsScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.telemetrySettingsEntry(navigator: Navigator) {
    entry<Screen.Settings.PrivacyDiagnostics> {
        TelemetrySettingsScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
