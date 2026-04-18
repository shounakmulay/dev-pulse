package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPExtendedFloatingActionButton
import dev.shounakmulay.core.designsystem.components.DPFilledIconButton
import dev.shounakmulay.core.designsystem.components.DPFilledTonalIconButton
import dev.shounakmulay.core.designsystem.components.DPFloatingActionButton
import dev.shounakmulay.core.designsystem.components.DPIconButton
import dev.shounakmulay.core.designsystem.components.DPLargeFloatingActionButton
import dev.shounakmulay.core.designsystem.components.DPOutlinedIconButton
import dev.shounakmulay.core.designsystem.components.DPSmallFloatingActionButton

@Composable
fun IconButtonsAndFabsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPBody(text = "Icon Buttons") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPIconButton(onClick = {}) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                }
                DPFilledIconButton(onClick = {}) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                }
                DPFilledTonalIconButton(onClick = {}) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                DPOutlinedIconButton(onClick = {}) {
                    Icon(Icons.Default.Share, contentDescription = null)
                }
            }
        }
        item { DPBody(text = "Icon Buttons — Disabled") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPIconButton(onClick = {}, enabled = false) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                }
                DPFilledIconButton(onClick = {}, enabled = false) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                }
                DPFilledTonalIconButton(onClick = {}, enabled = false) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                DPOutlinedIconButton(onClick = {}, enabled = false) {
                    Icon(Icons.Default.Share, contentDescription = null)
                }
            }
        }

        item { DPBody(text = "FABs") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DPSmallFloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
                DPFloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
                DPLargeFloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
        item {
            DPExtendedFloatingActionButton(
                text = { Text("Create") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = {},
            )
        }
    }
}
