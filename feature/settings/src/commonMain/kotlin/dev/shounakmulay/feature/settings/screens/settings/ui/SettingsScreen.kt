package dev.shounakmulay.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import dev.shounakmulay.core.designsystem.components.DPListItem
import dev.shounakmulay.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.resources.stringRes
import dev.shounakmulay.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.core.ui.screen.Screen
import devpulse.core.resources.generated.resources.developer_tools
import devpulse.core.resources.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        onEffect = {},
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.settings),
                navigationIcon = {
                    DPBackNavigationIconButton(navigator::navigateBack)
                }
            )
        },
    ) {
        Column {
            DPListItem(
                headlineText = stringResource(stringRes.developer_tools)
            )
        }
    }
}
