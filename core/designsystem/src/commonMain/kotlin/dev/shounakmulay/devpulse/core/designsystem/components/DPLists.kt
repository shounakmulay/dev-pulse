package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemElevation
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPDensity
import dev.shounakmulay.devpulse.core.designsystem.theme.DPElevationLevel
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.value
import dev.shounakmulay.devpulse.core.designsystem.theme.verticalPadding

// ---- Text-first static list item ----
@Composable
fun DPListItem(
    headlineText: String,
    modifier: Modifier = Modifier,
    overlineText: String? = null,
    supportingText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    density: DPDensity = DPDensity.Default,
    colors: ListItemColors? = null,
    tonalElevation: DPElevationLevel = DPElevationLevel.Level0,
    shadowElevation: DPElevationLevel = DPElevationLevel.Level0,
) {
    ListItem(
        headlineContent = { DPTextView(headlineText, variant = DPTextViewVariant.BodyMedium) },
        modifier = modifier,
        overlineContent = overlineText?.let {
            {
                DPTextView(
                    it,
                    variant = DPTextViewVariant.LabelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        supportingContent = supportingText?.let {
            {
                DPTextView(
                    it,
                    variant = DPTextViewVariant.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors ?: ListItemDefaults.colors(),
        tonalElevation = tonalElevation.value(),
        shadowElevation = shadowElevation.value(),
    )
}

// ---- Text-first clickable list item ----
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPListItem(
    headlineText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    overlineText: String? = null,
    supportingText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    selected: Boolean = false,
    density: DPDensity = DPDensity.Default,
    enabled: Boolean = true,
    colors: ListItemColors? = null,
) {
    val vPad = density.verticalPadding()
    val hPad = DPTheme.spacing.screenHorizontal
    val resolvedPadding = contentPadding ?: PaddingValues(horizontal = hPad, vertical = vPad)
    val resolvedColors = colors ?: ListItemDefaults.colors()

    if (selected) {
        ListItem(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            overlineContent = overlineText?.let {
                {
                    DPTextView(
                        it,
                        variant = DPTextViewVariant.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            supportingContent = supportingText?.let {
                {
                    DPTextView(
                        it,
                        variant = DPTextViewVariant.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            colors = resolvedColors,
            elevation = ListItemDefaults.elevation(),
            contentPadding = resolvedPadding,
            interactionSource = null,
            content = { DPTextView(headlineText, variant = DPTextViewVariant.BodyMedium) },
        )
    } else {
        ListItem(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            overlineContent = overlineText?.let {
                {
                    DPTextView(
                        it,
                        variant = DPTextViewVariant.LabelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            supportingContent = supportingText?.let {
                {
                    DPTextView(
                        it,
                        variant = DPTextViewVariant.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            colors = resolvedColors,
            elevation = ListItemDefaults.elevation(),
            contentPadding = resolvedPadding,
            interactionSource = null,
            content = { DPTextView(headlineText, variant = DPTextViewVariant.BodyMedium) },
        )
    }
}

// ---- Slot-based overload with branching logic (selected, onClick) ----

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPListItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    verticalAlignment: Alignment.Vertical = ListItemDefaults.verticalAlignment(),
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    shapes: ListItemShapes = ListItemDefaults.shapes(),
    colors: ListItemColors = ListItemDefaults.colors(),
    elevation: ListItemElevation = ListItemDefaults.elevation(),
    contentPadding: PaddingValues = ListItemDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    ListItem(
        selected = selected, onClick = onClick, modifier = modifier, enabled = enabled,
        leadingContent = leadingContent, trailingContent = trailingContent,
        overlineContent = overlineContent, supportingContent = supportingContent,
        verticalAlignment = verticalAlignment, onLongClick = onLongClick,
        onLongClickLabel = onLongClickLabel, shapes = shapes, colors = colors,
        elevation = elevation, contentPadding = contentPadding,
        interactionSource = interactionSource, content = content,
    )
}

@DPComponentPreview
@Composable
private fun DPListsPreview() {
    Preview {
        Column(modifier = Modifier.padding(16.dp)) {
            DPListItem(headlineText = "Default item")
            DPListItem(headlineText = "With supporting", supportingText = "Supporting text")
            DPListItem(headlineText = "Clickable", onClick = {})
            DPListItem(headlineText = "Selected", onClick = {}, selected = true)
        }
    }
}
