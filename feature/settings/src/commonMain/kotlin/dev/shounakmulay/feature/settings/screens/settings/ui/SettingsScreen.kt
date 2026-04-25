package dev.shounakmulay.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPIconButton
import dev.shounakmulay.core.designsystem.components.DPListItem
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.core.designsystem.theme.DPDensity
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import devpulse.core.resources.generated.resources.design_system_board
import devpulse.core.resources.generated.resources.developer_tools
import devpulse.core.resources.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        onEffect = {
            when (it) {
                SettingsEffect.NavigateToDesignSystemBoard -> navigator.navigate(Screen.DeveloperTools.DesignSystemBoard)
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
        Column {
            SectionHeading(
                title = stringResource(stringRes.developer_tools)
            )
            DPListItem(
                headlineText = stringResource(stringRes.design_system_board),
                supportingText = "Just supporting",
                onClick = {
                    viewModel.onEvent(SettingsEvent.NavigateToDesignSystemBoard)
                },
                density = DPDensity.Dense,
                trailingContent = {
                    DPIconButton(
                        icon = Icons.Default.ChevronRight,
                        contentDescription = "",
                    ) {
                        viewModel.onEvent(SettingsEvent.NavigateToDesignSystemBoard)
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeading(title: String) {
    DPTextView(
        modifier = Modifier.padding(16.dp),
        text = title,
        variant = DPTextViewVariant.TitleMedium
    )
}
