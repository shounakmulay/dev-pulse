package dev.shounakmulay.devpulse.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.shounakmulay.devpulse.core.designsystem.theme.AppTheme
import dev.shounakmulay.devpulse.theme.ThemeSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DevPulseThemedApp() {
    val themeSettingsViewModel = koinViewModel<ThemeSettingsViewModel>()
    val themeSettingsState by themeSettingsViewModel.state.collectAsState()
    val systemDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(systemDarkTheme) {
        themeSettingsViewModel.updateSystemDarkTheme(systemDarkTheme)
    }

    AppTheme(
        isBlackMode = themeSettingsState.isBlackMode,
        darkTheme = themeSettingsState.darkTheme
    ) {
        Scaffold {
            DevPulseNavApp()
        }
    }
}
