package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonScope
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = FloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPSmallFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.smallShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = SmallFloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPLargeFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.largeShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = LargeFloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPExtendedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.extendedFabShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) = ExtendedFloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPExtendedFloatingActionButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    shape: Shape = FloatingActionButtonDefaults.extendedFabShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
) = ExtendedFloatingActionButton(
    text = text,
    icon = icon,
    onClick = onClick,
    modifier = modifier,
    expanded = expanded,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    interactionSource = interactionSource,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPMediumFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.mediumShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) = MediumFloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPFloatingActionButtonMenu(
    expanded: Boolean,
    button: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    content: @Composable FloatingActionButtonMenuScope.() -> Unit,
) = FloatingActionButtonMenu(
    expanded = expanded,
    button = button,
    modifier = modifier,
    horizontalAlignment = horizontalAlignment,
    content = content,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPToggleFloatingActionButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: (Float) -> Color = ToggleFloatingActionButtonDefaults.containerColor(),
    contentAlignment: Alignment = Alignment.TopEnd,
    containerSize: (Float) -> Dp = ToggleFloatingActionButtonDefaults.containerSize(),
    containerCornerRadius: (Float) -> Dp =
        ToggleFloatingActionButtonDefaults.containerCornerRadius(),
    content: @Composable ToggleFloatingActionButtonScope.() -> Unit,
) = ToggleFloatingActionButton(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    containerColor = containerColor,
    contentAlignment = contentAlignment,
    containerSize = containerSize,
    containerCornerRadius = containerCornerRadius,
    content = content,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@DPComponentPreview
@Composable
private fun DPFABsPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DPFloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
            DPSmallFloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
            DPLargeFloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
            DPMediumFloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
            DPExtendedFloatingActionButton(onClick = {}) {
                Text("Action")
            }
            DPExtendedFloatingActionButton(
                text = { Text("Create") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = {},
            )
            DPToggleFloatingActionButton(checked = false, onCheckedChange = {}) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}
