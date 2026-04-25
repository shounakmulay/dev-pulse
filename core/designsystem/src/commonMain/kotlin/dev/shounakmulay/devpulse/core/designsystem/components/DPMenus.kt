package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.MenuItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.*

@Composable
fun DPDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape? = null,
    containerColor: Color? = null,
    tonalElevation: DPElevationLevel = DPElevationLevel.Level2,
    shadowElevation: DPElevationLevel = DPElevationLevel.Level2,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val resolvedShape = shape ?: MenuDefaults.shape
    val resolvedContainer = containerColor ?: MenuDefaults.containerColor
    val resolvedTonal = tonalElevation.value()
    val resolvedShadow = shadowElevation.value()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
        scrollState = scrollState,
        properties = properties,
        shape = resolvedShape,
        containerColor = resolvedContainer,
        tonalElevation = resolvedTonal,
        shadowElevation = resolvedShadow,
        border = border,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    supportingText: String? = null,
    intent: DPIntent = DPIntent.Neutral,
    enabled: Boolean = true,
    colors: MenuItemColors? = null,
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    val error = MaterialTheme.colorScheme.error
    val effectiveColors: MenuItemColors =
        when {
            intent == DPIntent.Danger && colors == null ->
                MenuDefaults.itemColors(
                    textColor = error,
                    leadingIconColor = error,
                )
            else -> colors ?: MenuDefaults.itemColors()
        }

    val leading: (@Composable () -> Unit)? = leadingIcon?.let { v -> { Icon(v, null, Modifier.size(DPTheme.iconSize.md)) } }
    val trailing: (@Composable () -> Unit)? = trailingIcon?.let { v -> { Icon(v, null, Modifier.size(DPTheme.iconSize.md)) } }
    val supporting: (@Composable () -> Unit)? = supportingText?.let { st -> { Text(st) } }

    if (supporting == null) {
        DropdownMenuItem(
            text = { Text(text) },
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leading,
            trailingIcon = trailing,
            enabled = enabled,
            colors = effectiveColors,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
        )
    } else {
        DropdownMenuItem(
            onClick = onClick,
            text = { Text(text) },
            shape = MenuDefaults.middleItemShape,
            modifier = modifier,
            leadingIcon = leading,
            trailingIcon = trailing,
            enabled = enabled,
            colors = effectiveColors,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
            supportingText = supporting,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPDropdownMenuItem(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuSelectableItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    DropdownMenuItem(
        onClick = onClick,
        text = text,
        shape = shape,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        supportingText = supportingText,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPDropdownMenuItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: @Composable () -> Unit,
    shapes: MenuItemShapes,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    checkedLeadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.selectableItemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuSelectableItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    DropdownMenuItem(
        checked = checked,
        onCheckedChange = onCheckedChange,
        text = text,
        shapes = shapes,
        modifier = modifier,
        leadingIcon = leadingIcon,
        checkedLeadingIcon = checkedLeadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        supportingText = supportingText,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPDropdownMenuItem(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    shapes: MenuItemShapes,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    selectedLeadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.selectableItemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuSelectableItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    DropdownMenuItem(
        selected = selected,
        onClick = onClick,
        text = text,
        shapes = shapes,
        modifier = modifier,
        leadingIcon = leadingIcon,
        selectedLeadingIcon = selectedLeadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        supportingText = supportingText,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@DPComponentPreview
@Composable
private fun DPMenusPreview() {
    Preview {
        var expanded by remember { mutableStateOf(true) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextField(
                value = "Option",
                onValueChange = {},
                modifier =
                    Modifier
                        .menuAnchor()
                        .padding(16.dp),
                readOnly = true,
            )
            DPDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DPDropdownMenuItem(text = "One", onClick = { expanded = false })
            }
        }
    }
}
