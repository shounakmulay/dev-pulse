@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonScope
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.DPSize
import dev.shounakmulay.core.designsystem.theme.DPTheme

enum class DPFABStyle { Primary, Secondary, Tertiary, Surface }

@Composable
@ReadOnlyComposable
private fun DPFABStyle.containerColor(): Color = when (this) {
    DPFABStyle.Primary -> MaterialTheme.colorScheme.primaryContainer
    DPFABStyle.Secondary -> MaterialTheme.colorScheme.secondaryContainer
    DPFABStyle.Tertiary -> MaterialTheme.colorScheme.tertiaryContainer
    DPFABStyle.Surface -> MaterialTheme.colorScheme.surfaceContainerHigh
}

@Composable
fun DPFAB(
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DPFABStyle = DPFABStyle.Primary,
    size: DPSize = DPSize.Medium,
    shape: Shape? = null,
    elevation: FloatingActionButtonElevation? = null,
) {
    val containerColor = style.containerColor()
    val contentColor = contentColorFor(containerColor)
    val resolvedElevation = elevation ?: FloatingActionButtonDefaults.elevation()
    val iconSlot: @Composable () -> Unit = {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }

    when (size) {
        DPSize.Small -> SmallFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = shape ?: FloatingActionButtonDefaults.smallShape,
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = resolvedElevation,
            content = iconSlot,
        )
        DPSize.Medium -> FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = shape ?: FloatingActionButtonDefaults.shape,
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = resolvedElevation,
            content = iconSlot,
        )
        DPSize.Large -> LargeFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = shape ?: FloatingActionButtonDefaults.largeShape,
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = resolvedElevation,
            content = iconSlot,
        )
    }
}

@Composable
fun DPExtendedFAB(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: DPFABStyle = DPFABStyle.Primary,
    expanded: Boolean = true,
    shape: Shape? = null,
    elevation: FloatingActionButtonElevation? = null,
) {
    val containerColor = style.containerColor()
    ExtendedFloatingActionButton(
        text = { Text(text) },
        icon = { if (icon != null) Icon(imageVector = icon, contentDescription = null) },
        onClick = onClick,
        modifier = modifier,
        expanded = expanded,
        shape = shape ?: FloatingActionButtonDefaults.extendedFabShape,
        containerColor = containerColor,
        contentColor = contentColorFor(containerColor),
        elevation = elevation ?: FloatingActionButtonDefaults.elevation(),
    )
}

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

@Composable
fun DPToggleFloatingActionButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: (Float) -> Color = ToggleFloatingActionButtonDefaults.containerColor(),
    contentAlignment: Alignment = Alignment.TopEnd,
    containerSize: (Float) -> Dp = ToggleFloatingActionButtonDefaults.containerSize(),
    containerCornerRadius: (Float) -> Dp = ToggleFloatingActionButtonDefaults.containerCornerRadius(),
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

@DPComponentPreview
@Composable
private fun DPFABStylesPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
        ) {
            DPFABStyle.entries.forEach { style ->
                DPFAB(icon = Icons.Default.Add, onClick = {}, style = style)
            }
            DPSize.entries.forEach { size ->
                DPFAB(icon = Icons.Default.Edit, onClick = {}, size = size)
            }
            DPExtendedFAB(text = "Create", icon = Icons.Default.Add, onClick = {})
            DPExtendedFAB(text = "Secondary", onClick = {}, style = DPFABStyle.Secondary)
        }
    }
}
