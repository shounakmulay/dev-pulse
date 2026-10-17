package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPSwitch
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview

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

@DPComponentPreview
@Composable
private fun SettingsTogglePreview() {
    Preview {
        Column {
            SettingsToggle(checked = true, headlineText = "Sync in background", supportingText = "Keep feeds up to date", onClick = {})
            SettingsToggle(checked = false, headlineText = "Black mode", supportingText = null, enabled = false, onClick = {})
        }
    }
}
