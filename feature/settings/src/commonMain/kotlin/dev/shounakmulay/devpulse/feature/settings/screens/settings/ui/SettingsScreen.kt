package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonGroup
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPSwitch
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import devpulse.core.resources.generated.resources.black_mode
import devpulse.core.resources.generated.resources.design_system_board
import devpulse.core.resources.generated.resources.developer_tools
import devpulse.core.resources.generated.resources.select_app_theme
import devpulse.core.resources.generated.resources.settings
import devpulse.core.resources.generated.resources.theme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        onEffect = {
            when (it) {
                SettingsScreenEffect.NavigateToDesignSystemBoard -> navigator.navigate(Screen.DeveloperTools.DesignSystemBoard)
                else -> viewModel.unhandledEffect(it)
            }
        },
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.settings),
                navigationIcon = {
                    DPBackNavigationIconButton(onNavigateBack = navigator::navigateBack)
                }
            )
        },
    ) {
        val canToggleBlackMode = canToggleBlackMode(isDarkTheme = DPTheme.isDarkTheme)

        LazyColumn {
            item {
                ThemeSettingsSection(
                    selectedThemeMode = themeMode,
                    isBlackMode = isBlackMode,
                    canToggleBlackMode = canToggleBlackMode,
                    onThemeModeSelected = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnThemeModeSelected(value))
                    },
                    onBlackModeToggled = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnBlackModeToggled(value))
                    }
                )
            }
            item {
                DeveloperSettingsSection(
                    onDesignSystemBoardClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnDesignSystemBoardClicked)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeSettingsSection(
    selectedThemeMode: ThemeMode,
    isBlackMode: Boolean,
    canToggleBlackMode: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onBlackModeToggled: (Boolean) -> Unit
) {
    SettingsSectionHeading(title = stringResource(stringRes.theme))
    ThemeModeSelector(
        title = stringResource(stringRes.select_app_theme),
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

@Composable
private fun DeveloperSettingsSection(
    onDesignSystemBoardClick: () -> Unit
) {
    SettingsSectionHeading(title = stringResource(stringRes.developer_tools))
    SettingsSubPageLink(
        headlineText = stringResource(stringRes.design_system_board),
        onClick = onDesignSystemBoardClick
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ThemeModeSelector(
    title: String,
    selectedThemeMode: ThemeMode,
    onValueSelected: (ThemeMode) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        DPTextView(
            modifier = Modifier.padding(bottom = 16.dp),
            text = title,
            variant = DPTextViewVariant.BodyMedium
        )
        DPButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            overflowIndicator = {},
        ) {
            ThemeMode.entries.forEach { themeMode ->
                val isSelected = themeMode == selectedThemeMode

                toggleableItem(
                    weight = if (isSelected) 1.5f else 1f,
                    checked = isSelected,
                    label = themeMode.label(),
                    onCheckedChange = { checked ->
                        if (checked) {
                            onValueSelected(themeMode)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    checked: Boolean,
    headlineText: String,
    supportingText: String?,
    enabled: Boolean,
    onClick: (Boolean) -> Unit
) {
    DPListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        enabled = enabled,
        onClick = {
            onClick(!checked)
        },
        trailingContent = {
            DPSwitch(
                checked = checked,
                onCheckedChange = {
                    onClick(it)
                },
                enabled = enabled
            )
        }
    )
}

private fun ThemeMode.label(): String =
    when (this) {
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
        ThemeMode.SYSTEM -> "System"
    }

@Composable
private fun SettingsSectionHeading(title: String) {
    DPTextView(
        modifier = Modifier.padding(16.dp),
        text = title,
        variant = DPTextViewVariant.TitleMedium
    )
}

@Composable
private fun SettingsSubPageLink(
    headlineText: String,
    onClick: () -> Unit
) {
    DPListItem(
        headlineText = headlineText,
        onClick = onClick,
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "",
            )
        }
    )
}
