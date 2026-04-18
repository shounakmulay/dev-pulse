package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.MenuItemShapes
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape = MenuDefaults.shape,
    containerColor: Color = MenuDefaults.containerColor,
    tonalElevation: androidx.compose.ui.unit.Dp = MenuDefaults.TonalElevation,
    shadowElevation: androidx.compose.ui.unit.Dp = MenuDefaults.ShadowElevation,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
        scrollState = scrollState,
        properties = properties,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        content = content,
    )
}

@Composable
fun DPDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    DropdownMenuItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPExposedDropdownMenuBox(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@DPComponentPreview
@Composable
private fun DPMenusPreview() {
    Preview {
        var expanded by remember { mutableStateOf(true) }
        DPExposedDropdownMenuBox(
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
                DPDropdownMenuItem(text = { Text("One") }, onClick = { expanded = false })
            }
        }
    }
}
