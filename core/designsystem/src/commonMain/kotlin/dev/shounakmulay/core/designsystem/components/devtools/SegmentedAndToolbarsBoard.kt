package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPHorizontalFloatingToolbar
import dev.shounakmulay.core.designsystem.components.DPMultiChoiceSegmentedButtonRow
import dev.shounakmulay.core.designsystem.components.DPSegmentedButton
import dev.shounakmulay.core.designsystem.components.DPSingleChoiceSegmentedButtonRow
import dev.shounakmulay.core.designsystem.components.DPVerticalFloatingToolbar

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SegmentedAndToolbarsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPBody(text = "Single Choice Segmented Button")
            Spacer(Modifier.height(8.dp))
            var selected by remember { mutableIntStateOf(0) }
            val options = listOf("Day", "Week", "Month")
            DPSingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    DPSegmentedButton(
                        selected = selected == index,
                        onClick = { selected = index },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        label = { Text(label) },
                    )
                }
            }
        }

        item {
            DPBody(text = "Multi Choice Segmented Button")
            Spacer(Modifier.height(8.dp))
            var bold by remember { mutableStateOf(true) }
            var italic by remember { mutableStateOf(false) }
            var underline by remember { mutableStateOf(false) }
            DPMultiChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                DPSegmentedButton(
                    checked = bold,
                    onCheckedChange = { bold = it },
                    shape = SegmentedButtonDefaults.itemShape(0, 3),
                    label = { Text("B") },
                )
                DPSegmentedButton(
                    checked = italic,
                    onCheckedChange = { italic = it },
                    shape = SegmentedButtonDefaults.itemShape(1, 3),
                    label = { Text("I") },
                )
                DPSegmentedButton(
                    checked = underline,
                    onCheckedChange = { underline = it },
                    shape = SegmentedButtonDefaults.itemShape(2, 3),
                    label = { Text("U") },
                )
            }
        }

        item {
            DPBody(text = "Horizontal Floating Toolbar")
            Spacer(Modifier.height(8.dp))
            DPHorizontalFloatingToolbar(expanded = true) {
                Text("Cut")
                Text("Copy")
                Text("Paste")
            }
        }

        item {
            DPBody(text = "Horizontal Floating Toolbar with FAB")
            Spacer(Modifier.height(8.dp))
            DPHorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingToolbarDefaults.StandardFloatingActionButton(onClick = {}) {
                        Text("+")
                    }
                },
            ) {
                Text("Action A")
                Text("Action B")
            }
        }

        item {
            DPBody(text = "Vertical Floating Toolbar")
            Spacer(Modifier.height(8.dp))
            DPVerticalFloatingToolbar(expanded = true) {
                Text("1")
                Text("2")
                Text("3")
            }
        }
    }
}
