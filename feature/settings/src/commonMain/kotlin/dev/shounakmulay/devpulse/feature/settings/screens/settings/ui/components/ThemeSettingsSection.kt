package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.black_mode
import devpulse.core.resources.generated.resources.theme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ThemeSettingsSection(
    selectedThemeMode: ThemeMode,
    isBlackMode: Boolean,
    canToggleBlackMode: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onBlackModeToggled: (Boolean) -> Unit
) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.theme))
        ThemeModeSelector(
            selectedThemeMode = selectedThemeMode,
            onValueSelected = onThemeModeSelected
        )
        SettingsToggle(
            checked = isBlackMode,
            headlineText = stringResource(stringRes.black_mode),
            supportingText = null,
            enabled = canToggleBlackMode,
            onClick = onBlackModeToggled
        )
    }
}