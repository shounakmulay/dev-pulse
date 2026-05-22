package dev.shounakmulay.devpulse.core.designsystem.components

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
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme

enum class DPChipKind { Assist, Filter, Input, Suggestion }

enum class DPChipStyle { Flat, Elevated }

@Composable
fun DPChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    kind: DPChipKind = DPChipKind.Assist,
    style: DPChipStyle = DPChipStyle.Flat,
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
    val label: @Composable () -> Unit = { DPTextView(text = text, variant = DPTextViewVariant.LabelMedium) }
    when (kind) {
        DPChipKind.Assist -> {
            when (style) {
                DPChipStyle.Flat -> {
                    AssistChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: AssistChipDefaults.shape,
                        colors = chipColors ?: AssistChipDefaults.assistChipColors(),
                        elevation = chipElevation ?: AssistChipDefaults.assistChipElevation(),
                        border = border ?: AssistChipDefaults.assistChipBorder(enabled),
                        horizontalArrangement = AssistChipDefaults.horizontalArrangement(),
                        contentPadding = AssistChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
                DPChipStyle.Elevated -> {
                    ElevatedAssistChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: AssistChipDefaults.shape,
                        colors = chipColors ?: AssistChipDefaults.elevatedAssistChipColors(),
                        elevation = chipElevation ?: AssistChipDefaults.elevatedAssistChipElevation(),
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
                    FilterChip(
                        selected = selected,
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: FilterChipDefaults.shape,
                        colors = selectableChipColors ?: FilterChipDefaults.filterChipColors(),
                        elevation = selectableChipElevation ?: FilterChipDefaults.filterChipElevation(),
                        border = border ?: FilterChipDefaults.filterChipBorder(enabled, selected),
                        horizontalArrangement = FilterChipDefaults.horizontalArrangement(),
                        contentPadding = FilterChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
                DPChipStyle.Elevated -> {
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        leadingIcon = leadingIcon.asLeading18(),
                        trailingIcon = trailingIcon.asTrailing18(),
                        shape = shape ?: FilterChipDefaults.shape,
                        colors = selectableChipColors ?: FilterChipDefaults.elevatedFilterChipColors(),
                        elevation = selectableChipElevation ?: FilterChipDefaults.elevatedFilterChipElevation(),
                        border = border,
                        horizontalArrangement = FilterChipDefaults.horizontalArrangement(),
                        contentPadding = FilterChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
            }
        }
        DPChipKind.Input -> {
            val trailing = inputTrailingContent(onDismiss, trailingIcon)
            val leading = leadingIcon.asLeading18()
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
                colors = selectableChipColors ?: InputChipDefaults.inputChipColors(),
                elevation = selectableChipElevation ?: InputChipDefaults.inputChipElevation(),
                border = border ?: InputChipDefaults.inputChipBorder(enabled, selected),
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
                    SuggestionChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        icon = leadingIcon.asSuggestionIcon(),
                        shape = shape ?: SuggestionChipDefaults.shape,
                        colors = chipColors ?: SuggestionChipDefaults.suggestionChipColors(),
                        elevation = chipElevation ?: SuggestionChipDefaults.suggestionChipElevation(),
                        border = border ?: SuggestionChipDefaults.suggestionChipBorder(enabled),
                        horizontalArrangement = SuggestionChipDefaults.horizontalArrangement(),
                        contentPadding = SuggestionChipDefaults.ContentPadding,
                        interactionSource = null,
                    )
                }
                DPChipStyle.Elevated -> {
                    ElevatedSuggestionChip(
                        onClick = onClick,
                        label = label,
                        modifier = modifier,
                        enabled = enabled,
                        icon = leadingIcon.asSuggestionIcon(),
                        shape = shape ?: SuggestionChipDefaults.shape,
                        colors = chipColors ?: SuggestionChipDefaults.elevatedSuggestionChipColors(),
                        elevation = chipElevation ?: SuggestionChipDefaults.elevatedSuggestionChipElevation(),
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
                        DPChip(text = "$kind", onClick = {}, kind = kind, style = style)
                        DPChip(text = "Selected", onClick = {}, kind = kind, style = style, selected = true)
                    }
                }
            }
        }
    }
}
