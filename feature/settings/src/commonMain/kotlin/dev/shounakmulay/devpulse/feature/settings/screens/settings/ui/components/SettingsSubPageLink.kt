package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons

@Composable
internal fun SettingsSubPageLink(
    headlineText: String,
    onClick: () -> Unit
) {
    Column {
        DPListItem(
            headlineText = headlineText,
            onClick = onClick,
            trailingContent = {
                Icon(
                    imageVector = DPIcons.ChevronRight,
                    contentDescription = "",
                )
            }
        )
    }
}
