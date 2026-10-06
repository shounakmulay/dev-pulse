package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPSwitch

@Composable
internal fun SettingsToggle(
    checked: Boolean,
    headlineText: String,
    supportingText: String?,
    enabled: Boolean = true,
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