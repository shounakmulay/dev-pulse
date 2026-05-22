package dev.shounakmulay.devpulse.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.shounakmulay.devpulse.core.designsystem.theme.AppTheme
import dev.shounakmulay.devpulse.theme.ThemeSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DevPulseThemedApp() {
    val themeSettingsViewModel = koinViewModel<ThemeSettingsViewModel>()
    val themeSettingsState by themeSettingsViewModel.state
    val systemDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(systemDarkTheme) {
        themeSettingsViewModel.updateSystemDarkTheme(systemDarkTheme)
    }

    val colorScheme by remember(themeSettingsState, systemDarkTheme) {
        derivedStateOf {
            themeSettingsViewModel.getColorScheme(
                themeSettingsState,
                isSystemInDarkTheme = systemDarkTheme
            )
        }
    }

    val isDarkTheme by remember(themeSettingsState) {
        derivedStateOf {
            themeSettingsState.isDarkMode()
        }
    }

    AppTheme(
        colorScheme = colorScheme,
        isDarkTheme = isDarkTheme,
    ) {
        Scaffold {
            DevPulseNavApp()
        }
    }
}
