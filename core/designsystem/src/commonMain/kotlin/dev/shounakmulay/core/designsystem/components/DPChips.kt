package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.DPTheme
import dev.shounakmulay.core.designsystem.theme.DPIntent
import dev.shounakmulay.core.designsystem.theme.colors

enum class DPChipKind { Assist, Filter, Input, Suggestion }

enum class DPChipStyle { Flat, Elevated }

@Composable
fun DPChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    kind: DPChipKind = DPChipKind.Assist,
    style: DPChipStyle = DPChipStyle.Flat,
    intent: DPIntent = DPIntent.Primary,
    selected: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    avatar: @Composable (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape? = null,
    chipColors: ChipColors? = null,
    selectableChipColors: SelectableChipColors? = null,
    chipElevation: ChipElevation? = null,
    selectableChipElevation: SelectableChipElevation? = null,
    border: BorderStroke? = null,
) {
    val label: @Composable () -> Unit = { DPLabel(text = text, size = DPTextSize.Medium) }
    when (kind) {
        DPChipKind.Assist -> {
            when (style) {
                DPChipStyle.Flat -> {
                    val c = intent.colors()
                    val colors = chipColors
                        ?: AssistChipDefaults.assistChipColors(
                            labelColor = c.onContainer,
                            leadingIconContentColor = c.accent,
                        )
                    val elevation = chipElevation ?: AssistChipDefaults.assistChipElevation()
                    AssistChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: AssistChipDefaults.shape,
                        colors = colors,
                        elevation = elevation,
                        border = border ?: AssistChipDefaults.assistChipBorder(enabled),
                        horizontalArrangement = AssistChipDefaults.horizontalArrangement(),
                        contentPadding = AssistChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
                DPChipStyle.Elevated -> {
                    val c = intent.colors()
                    val colors = chipColors
                        ?: AssistChipDefaults.elevatedAssistChipColors(
                            labelColor = c.onContainer,
                            leadingIconContentColor = c.accent,
                        )
                    val elevation = chipElevation
                        ?: AssistChipDefaults.elevatedAssistChipElevation()
                    ElevatedAssistChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: AssistChipDefaults.shape,
                        colors = colors,
                        elevation = elevation,
                        border = border,
                        horizontalArrangement = AssistChipDefaults.horizontalArrangement(),
                        contentPadding = AssistChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
            }
        }
        DPChipKind.Filter -> {
            when (style) {
                DPChipStyle.Flat -> {
                    val c = intent.colors()
                    val colors = selectableChipColors
                        ?: FilterChipDefaults.filterChipColors(
                            selectedContainerColor = c.container,
                            selectedLabelColor = c.onContainer,
                            selectedLeadingIconColor = c.onContainer,
                        )
                    val elevation = selectableChipElevation
                        ?: FilterChipDefaults.filterChipElevation()
                    FilterChip(
                        selected = selected,
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: FilterChipDefaults.shape,
                        colors = colors,
                        elevation = elevation,
                        border = border
                            ?: FilterChipDefaults.filterChipBorder(enabled, selected),
                        horizontalArrangement = FilterChipDefaults.horizontalArrangement(),
                        contentPadding = FilterChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
                DPChipStyle.Elevated -> {
                    val c = intent.colors()
                    val colors = selectableChipColors
                        ?: FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = c.container,
                            selectedLabelColor = c.onContainer,
                            selectedLeadingIconColor = c.onContainer,
                        )
                    val elevation = selectableChipElevation
                        ?: FilterChipDefaults.elevatedFilterChipElevation()
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: FilterChipDefaults.shape,
                        colors = colors,
                        elevation = elevation,
                        border = border,
                        horizontalArrangement = FilterChipDefaults.horizontalArrangement(),
                        contentPadding = FilterChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
            }
        }
        DPChipKind.Input -> {
            val c = intent.colors()
            val colors = selectableChipColors
                ?: InputChipDefaults.inputChipColors(
                    selectedContainerColor = c.container,
                    selectedLabelColor = c.onContainer,
                    selectedLeadingIconColor = c.onContainer,
                )
            val trailing = inputTrailingContent(onDismiss, trailingIcon)
            val leading = leadingIcon.asLeading18()
            val elevation = selectableChipElevation
                ?: InputChipDefaults.inputChipElevation()
            InputChip(
                selected = selected,
                onClick = onClick,
                label = label,
                modifier = modifier,
                enabled = enabled,
                leadingIcon = leading,
                avatar = avatar,
                trailingIcon = trailing,
                shape = shape ?: InputChipDefaults.shape,
                colors = colors,
                elevation = elevation,
                border = border
                    ?: InputChipDefaults.inputChipBorder(enabled, selected),
                horizontalArrangement = InputChipDefaults.horizontalArrangement(),
                contentPadding = InputChipDefaults.contentPadding(
                    hasAvatar = avatar != null,
                    hasLeadingIcon = leading != null,
                    hasTrailingIcon = trailing != null,
                ),
                interactionSource = null,
            )
        }
        DPChipKind.Suggestion -> {
            when (style) {
                DPChipStyle.Flat -> {
                    val c = intent.colors()
                    val colors = chipColors
                        ?: SuggestionChipDefaults.suggestionChipColors(
                            labelColor = c.onContainer,
                            iconContentColor = c.accent,
                        )
                    val elevation = chipElevation
                        ?: SuggestionChipDefaults.suggestionChipElevation()
                    SuggestionChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        icon = leadingIcon.asSuggestionIcon(),
                        shape = shape ?: SuggestionChipDefaults.shape,
                        colors = colors,
                        elevation = elevation,
                        border = border
                            ?: SuggestionChipDefaults.suggestionChipBorder(enabled),
                        horizontalArrangement = SuggestionChipDefaults.horizontalArrangement(),
                        contentPadding = SuggestionChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
                DPChipStyle.Elevated -> {
                    val c = intent.colors()
                    val colors = chipColors
                        ?: SuggestionChipDefaults.elevatedSuggestionChipColors(
                            labelColor = c.onContainer,
                            iconContentColor = c.accent,
                        )
                    val elevation = chipElevation
                        ?: SuggestionChipDefaults.elevatedSuggestionChipElevation()
                    ElevatedSuggestionChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        icon = leadingIcon.asSuggestionIcon(),
                        shape = shape ?: SuggestionChipDefaults.shape,
                        colors = colors,
                        elevation = elevation,
                        border = border,
                        horizontalArrangement = SuggestionChipDefaults.horizontalArrangement(),
                        contentPadding = SuggestionChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
            }
        }
    }
}

private fun ImageVector?.asLeading18(): @Composable (() -> Unit)? =
    this?.let { v ->
        { Icon(v, contentDescription = null, modifier = Modifier.size(18.dp)) }
    }

private fun ImageVector?.asTrailing18(): @Composable (() -> Unit)? =
    this?.let { v ->
        { Icon(v, contentDescription = null, modifier = Modifier.size(18.dp)) }
    }

private fun ImageVector?.asSuggestionIcon(): @Composable (() -> Unit)? =
    this?.let { v ->
        { Icon(v, contentDescription = null) }
    }

private fun inputTrailingContent(
    onDismiss: (() -> Unit)?,
    trailingVector: ImageVector?,
): @Composable (() -> Unit)? {
    onDismiss?.let { dismiss ->
        return {
            IconButton(onClick = dismiss) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    }
    return trailingVector.asTrailing18()
}

@OptIn(ExperimentalLayoutApi::class)
@DPComponentPreview
@Composable
private fun DPChipMatrixPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPChipKind.entries.forEach { kind ->
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
                ) {
                    DPChipStyle.entries.forEach { style ->
                        DPChip(
                            text = "$kind",
                            onClick = {},
                            kind = kind,
                            style = style,
                        )
                        DPChip(
                            text = "Selected",
                            onClick = {},
                            kind = kind,
                            style = style,
                            selected = true,
                            intent = DPIntent.Primary,
                        )
                    }
                }
            }
        }
    }
}
