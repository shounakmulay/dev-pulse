package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPChip
import dev.shounakmulay.devpulse.core.designsystem.components.DPChipKind
import dev.shounakmulay.devpulse.core.designsystem.components.DPChipStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

private val allCategories = listOf(
    "Android",
    "Kotlin",
    "iOS",
    "Swift",
    "Flutter",
    "React Native",
    "Web",
    "DevOps",
    "AI",
    "General",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPTextView(text = "Assist Chips", variant = DPTextViewVariant.BodyMedium) }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPChip(
                    text = "Assist",
                    onClick = {},
                    kind = DPChipKind.Assist,
                    style = DPChipStyle.Flat
                )
                DPChip(
                    text = "Disabled",
                    onClick = {},
                    kind = DPChipKind.Assist,
                    style = DPChipStyle.Flat,
                    enabled = false,
                )
                DPChip(
                    text = "Elevated Assist",
                    onClick = {},
                    kind = DPChipKind.Assist,
                    style = DPChipStyle.Elevated,
                )
            }
        }

        item { DPTextView(text = "Filter Chips", variant = DPTextViewVariant.BodyMedium) }
        item {
            var selected by remember { mutableStateOf(false) }
            var elevatedSelected by remember { mutableStateOf(true) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPChip(
                    text = "Filter",
                    onClick = { selected = !selected },
                    kind = DPChipKind.Filter,
                    style = DPChipStyle.Flat,
                    selected = selected,
                    leadingIcon = if (selected) Icons.Default.Done else null,
                )
                DPChip(
                    text = "Elevated Filter",
                    onClick = { elevatedSelected = !elevatedSelected },
                    kind = DPChipKind.Filter,
                    style = DPChipStyle.Elevated,
                    selected = elevatedSelected,
                )
            }
        }

        item { DPTextView(text = "Input Chips", variant = DPTextViewVariant.BodyMedium) }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPChip(
                    text = "Input",
                    onClick = {},
                    kind = DPChipKind.Input,
                    style = DPChipStyle.Flat,
                    selected = true,
                    onDismiss = {},
                )
                DPChip(
                    text = "Unselected",
                    onClick = {},
                    kind = DPChipKind.Input,
                    style = DPChipStyle.Flat,
                    selected = false,
                )
            }
        }

        item { DPTextView(text = "Suggestion Chips", variant = DPTextViewVariant.BodyMedium) }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DPChip(
                    text = "Suggestion",
                    onClick = {},
                    kind = DPChipKind.Suggestion,
                    style = DPChipStyle.Flat
                )
                DPChip(
                    text = "Elevated",
                    onClick = {},
                    kind = DPChipKind.Suggestion,
                    style = DPChipStyle.Elevated,
                )
            }
        }
    }
}
