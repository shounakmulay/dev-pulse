package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.design_system_board
import devpulse.core.resources.generated.resources.developer_tools
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeveloperSettingsSection(
    onDesignSystemBoardClick: () -> Unit
) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.developer_tools))
        SettingsSubPageLink(
            headlineText = stringResource(stringRes.design_system_board),
            onClick = onDesignSystemBoardClick
        )
    }
}