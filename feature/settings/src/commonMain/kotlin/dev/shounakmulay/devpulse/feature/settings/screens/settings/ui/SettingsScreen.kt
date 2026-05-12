package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import dev.shounakmulay.devpulse.core.designsystem.theme.DPDensity
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.domain.models.ThemeMode
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.SettingsListItem
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.ThemeSingleChoiceOptions
import devpulse.core.resources.generated.resources.settings
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
            items(settingsList) {
                when (it) {
                    is SettingsListItem.SectionHeading -> SettingsSectionHeading(title = it.title.asString())
                    is SettingsListItem.SingleChoice -> SettingsSingleChoice(
                        title = it.title,
                        item = it,
                        selectedThemeMode = themeMode,
                        onValueSelected = { value ->
                            viewModel.onEvent(SettingsScreenEvent.OnSingleChoiceUpdated(it, value))
                        }
                    )

                    is SettingsListItem.SubPageLink -> SettingsSubPageLink(
                        headlineText = it.heading.asString(),
                        supportingText = it.supportingText?.asString(),
                        onClick = {
                            viewModel.onEvent(SettingsScreenEvent.OnSubPageLinkClick(it.linkToScreen))
                        }
                    )

                    is SettingsListItem.Toggle -> SettingsToggle(
                        checked = isBlackMode,
                        headlineText = it.title.asString(),
                        supportingText = null,
                        enabled = canToggleBlackMode,
                        onClick = { value ->
                            viewModel.onEvent(SettingsScreenEvent.OnListItemToggled(it, value))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsSingleChoice(
    title: TextResource,
    item: SettingsListItem.SingleChoice,
    selectedThemeMode: ThemeMode,
    onValueSelected: (ThemeSingleChoiceOptions) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        DPTextView(
            modifier = Modifier.padding(bottom = 16.dp),
            text = title.asString(),
            variant = DPTextViewVariant.BodyMedium
        )
        DPButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            overflowIndicator = {},
        ) {
            item.key.values.forEach { option ->
                val themeOption = option as? ThemeSingleChoiceOptions ?: return@forEach
                toggleableItem(
                    weight = 1f,
                    checked = themeOption.toThemeMode() == selectedThemeMode,
                    label = themeOption.label(),
                    onCheckedChange = { checked ->
                        if (checked) {
                            onValueSelected(themeOption)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsToggle(
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

private fun ThemeSingleChoiceOptions.label(): String =
    when (this) {
        ThemeSingleChoiceOptions.LIGHT -> "Light"
        ThemeSingleChoiceOptions.DARK -> "Dark"
        ThemeSingleChoiceOptions.SYSTEM -> "System"
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
    supportingText: String? = null,
    onClick: () -> Unit
) {
    DPListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        onClick = onClick,
        density = DPDensity.Dense,
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "",
            )
        }
    )
}
