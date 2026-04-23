package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPFAB
import dev.shounakmulay.core.designsystem.components.DPFABStyle
import dev.shounakmulay.core.designsystem.components.DPExtendedFAB
import dev.shounakmulay.core.designsystem.components.DPIconButton
import dev.shounakmulay.core.designsystem.components.DPIconButtonStyle
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.core.designsystem.theme.DPIntent
import dev.shounakmulay.core.designsystem.theme.DPSize

@Composable
fun IconButtonsAndFabsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPTextView(text = "Icon Buttons – styles", variant = DPTextViewVariant.BodyMedium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPIconButtonStyle.entries.forEach { style ->
                    DPIconButton(
                        icon = Icons.Default.Favorite,
                        contentDescription = null,
                        onClick = {},
                        style = style,
                    )
                }
            }
        }

        item { DPTextView(text = "Icon Buttons – sizes", variant = DPTextViewVariant.BodyMedium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPSize.entries.forEach { size ->
                    DPIconButton(
                        icon = Icons.Default.Favorite,
                        contentDescription = null,
                        onClick = {},
                        style = DPIconButtonStyle.Filled,
                        size = size,
                    )
                }
            }
        }

        item { DPTextView(text = "Icon Buttons – disabled", variant = DPTextViewVariant.BodyMedium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPIconButtonStyle.entries.forEach { style ->
                    DPIconButton(
                        icon = Icons.Default.Favorite,
                        contentDescription = null,
                        onClick = {},
                        style = style,
                        enabled = false,
                    )
                }
            }
        }

        item { DPTextView(text = "FABs – styles", variant = DPTextViewVariant.BodyMedium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DPFABStyle.entries.forEach { style ->
                    DPFAB(icon = Icons.Default.Add, onClick = {}, style = style)
                }
            }
        }

        item { DPTextView(text = "FABs – sizes", variant = DPTextViewVariant.BodyMedium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DPSize.entries.forEach { size ->
                    DPFAB(icon = Icons.Default.Add, onClick = {}, size = size)
                }
            }
        }

        item {
            DPExtendedFAB(text = "Create", icon = Icons.Default.Add, onClick = {})
        }
    }
}
