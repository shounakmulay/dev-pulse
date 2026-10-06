package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons

@Composable
fun SettingsExternalPageLink(
    headlineText: String,
    supportingText: String? = null,
    onClick: () -> Unit
) {
    DPListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        onClick = onClick,
        trailingContent = {
            Icon(
                imageVector = DPIcons.ExternalLink,
                contentDescription = "",
            )
        }
    )
}