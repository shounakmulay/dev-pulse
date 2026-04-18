package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPAssistChip
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPCategoryChip
import dev.shounakmulay.core.designsystem.components.DPElevatedAssistChip
import dev.shounakmulay.core.designsystem.components.DPElevatedFilterChip
import dev.shounakmulay.core.designsystem.components.DPElevatedSuggestionChip
import dev.shounakmulay.core.designsystem.components.DPFilterChip
import dev.shounakmulay.core.designsystem.components.DPInputChip
import dev.shounakmulay.core.designsystem.components.DPLabelChip
import dev.shounakmulay.core.designsystem.components.DPSuggestionChip

private val allCategories = listOf(
    "Android", "Kotlin", "iOS", "Swift", "Flutter", "React Native", "Web", "DevOps", "AI", "General",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPBody(text = "Assist Chips") }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPAssistChip(onClick = {}, label = { Text("Assist") })
                DPAssistChip(onClick = {}, label = { Text("Disabled") }, enabled = false)
                DPElevatedAssistChip(onClick = {}, label = { Text("Elevated Assist") })
            }
        }

        item { DPBody(text = "Filter Chips") }
        item {
            var selected by remember { mutableStateOf(false) }
            var elevatedSelected by remember { mutableStateOf(true) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPFilterChip(
                    selected = selected,
                    onClick = { selected = !selected },
                    label = { Text("Filter") },
                    leadingIcon = if (selected) {
                        { Icon(Icons.Default.Done, contentDescription = null) }
                    } else {
                        null
                    },
                )
                DPElevatedFilterChip(
                    selected = elevatedSelected,
                    onClick = { elevatedSelected = !elevatedSelected },
                    label = { Text("Elevated Filter") },
                )
            }
        }

        item { DPBody(text = "Input Chips") }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPInputChip(
                    selected = true,
                    onClick = {},
                    label = { Text("Input") },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null) },
                )
                DPInputChip(
                    selected = false,
                    onClick = {},
                    label = { Text("Unselected") },
                )
            }
        }

        item { DPBody(text = "Suggestion Chips") }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPSuggestionChip(onClick = {}, label = { Text("Suggestion") })
                DPElevatedSuggestionChip(onClick = {}, label = { Text("Elevated") })
            }
        }

        item { DPBody(text = "Category Chips") }
        item {
            var selectedCategory by remember { mutableStateOf<String?>(null) }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allCategories.forEach { category ->
                    DPCategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                    )
                }
            }
        }

        item { DPBody(text = "Label Chips") }
        item {
            var selectedLabel by remember { mutableStateOf<String?>("Selected") }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Default", "Selected", "Feature", "Bug", "Docs").forEach { label ->
                    DPLabelChip(
                        label = label,
                        isSelected = selectedLabel == label,
                        onClick = {
                            selectedLabel = if (selectedLabel == label) null else label
                        },
                    )
                }
            }
        }
    }
}
