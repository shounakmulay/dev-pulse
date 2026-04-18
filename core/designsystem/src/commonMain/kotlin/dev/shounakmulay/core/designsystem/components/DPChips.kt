package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPAssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = AssistChipDefaults.shape,
    colors: ChipColors = AssistChipDefaults.assistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.assistChipElevation(),
    border: BorderStroke? = AssistChipDefaults.assistChipBorder(enabled),
    horizontalArrangement: Arrangement.Horizontal = AssistChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues = AssistChipDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPElevatedAssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = AssistChipDefaults.shape,
    colors: ChipColors = AssistChipDefaults.elevatedAssistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.elevatedAssistChipElevation(),
    border: BorderStroke? = null,
    horizontalArrangement: Arrangement.Horizontal = AssistChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues = AssistChipDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    ElevatedAssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = FilterChipDefaults.shape,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.filterChipElevation(),
    border: BorderStroke? = FilterChipDefaults.filterChipBorder(enabled, selected),
    horizontalArrangement: Arrangement.Horizontal = FilterChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues = FilterChipDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPElevatedFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = FilterChipDefaults.shape,
    colors: SelectableChipColors = FilterChipDefaults.elevatedFilterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.elevatedFilterChipElevation(),
    border: BorderStroke? = null,
    horizontalArrangement: Arrangement.Horizontal = FilterChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues = FilterChipDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    ElevatedFilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPInputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    avatar: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = InputChipDefaults.shape,
    colors: SelectableChipColors = InputChipDefaults.inputChipColors(),
    elevation: SelectableChipElevation? = InputChipDefaults.inputChipElevation(),
    border: BorderStroke? = InputChipDefaults.inputChipBorder(enabled, selected),
    horizontalArrangement: Arrangement.Horizontal = InputChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues =
        InputChipDefaults.contentPadding(avatar != null, leadingIcon != null, trailingIcon != null),
    interactionSource: MutableInteractionSource? = null,
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        avatar = avatar,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPSuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = SuggestionChipDefaults.shape,
    colors: ChipColors = SuggestionChipDefaults.suggestionChipColors(),
    elevation: ChipElevation? = SuggestionChipDefaults.suggestionChipElevation(),
    border: BorderStroke? = SuggestionChipDefaults.suggestionChipBorder(enabled),
    horizontalArrangement: Arrangement.Horizontal = SuggestionChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues = SuggestionChipDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    SuggestionChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPElevatedSuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = SuggestionChipDefaults.shape,
    colors: ChipColors = SuggestionChipDefaults.elevatedSuggestionChipColors(),
    elevation: ChipElevation? = SuggestionChipDefaults.elevatedSuggestionChipElevation(),
    border: BorderStroke? = null,
    horizontalArrangement: Arrangement.Horizontal = SuggestionChipDefaults.horizontalArrangement(),
    contentPadding: PaddingValues = SuggestionChipDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    ElevatedSuggestionChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@DPComponentPreview
@Composable
private fun DPChipsPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPAssistChip(onClick = {}, label = { Text("Assist") })
                DPElevatedAssistChip(onClick = {}, label = { Text("Elevated assist") })
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPFilterChip(selected = false, onClick = {}, label = { Text("Filter") })
                DPFilterChip(selected = true, onClick = {}, label = { Text("On") })
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPElevatedFilterChip(selected = false, onClick = {}, label = { Text("El. filter") })
                DPElevatedFilterChip(selected = true, onClick = {}, label = { Text("El. tonal") })
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPInputChip(
                    selected = true,
                    onClick = {},
                    label = { Text("Input") },
                    trailingIcon = {
                        Icon(Icons.Default.Close, contentDescription = null)
                    },
                )
                DPInputChip(
                    selected = false,
                    onClick = {},
                    label = { Text("Input tonal") },
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSuggestionChip(onClick = {}, label = { Text("Suggestion") })
                DPElevatedSuggestionChip(onClick = {}, label = { Text("Elevated suggestion") })
            }
        }
    }
}
