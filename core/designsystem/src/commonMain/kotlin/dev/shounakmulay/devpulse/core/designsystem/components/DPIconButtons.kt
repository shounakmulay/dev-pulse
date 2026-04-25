@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPIntent
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.colors
import dev.shounakmulay.devpulse.core.designsystem.theme.iconSize

enum class DPIconButtonStyle { Standard, Filled, Tonal, Outlined }

@Composable
@ReadOnlyComposable
private fun dpIconButtonBorderWidth() = 1.dp

private fun iconButtonContainerSize(dpSize: DPSize): DpSize =
    when (dpSize) {
        DPSize.Small -> IconButtonDefaults.smallContainerSize()
        DPSize.Medium -> IconButtonDefaults.mediumContainerSize()
        DPSize.Large -> IconButtonDefaults.largeContainerSize()
    }

private fun Modifier.dpIconButtonBox(dpSize: DPSize): Modifier {
    val s = iconButtonContainerSize(dpSize)
    return this.size(s.width, s.height)
}

@Composable
private fun dpIconButtonDefaultShape(style: DPIconButtonStyle): Shape =
    when (style) {
        DPIconButtonStyle.Standard -> IconButtonDefaults.standardShape
        DPIconButtonStyle.Filled, DPIconButtonStyle.Tonal -> IconButtonDefaults.filledShape
        DPIconButtonStyle.Outlined -> IconButtonDefaults.outlinedShape
    }

@Composable
private fun dpIconButtonColors(
    style: DPIconButtonStyle,
    intent: DPIntent,
): IconButtonColors {
    val c = intent.colors()
    return when (style) {
        DPIconButtonStyle.Standard ->
            IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = c.accent,
            )
        DPIconButtonStyle.Filled ->
            IconButtonDefaults.filledIconButtonColors(
                containerColor = c.accent,
                contentColor = c.onAccent,
            )
        DPIconButtonStyle.Tonal ->
            IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = c.container,
                contentColor = c.onContainer,
            )
        DPIconButtonStyle.Outlined ->
            IconButtonDefaults.outlinedIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = c.accent,
            )
    }
}

@Composable
@ReadOnlyComposable
private fun dpIconButtonBorder(
    style: DPIconButtonStyle,
    intent: DPIntent,
    border: BorderStroke?,
): BorderStroke? {
    if (border != null) return border
    if (style != DPIconButtonStyle.Outlined) return null
    return BorderStroke(dpIconButtonBorderWidth(), intent.colors().outline)
}

@Composable
private fun dpIconToggleButtonColors(
    style: DPIconButtonStyle,
    intent: DPIntent,
): IconToggleButtonColors {
    val c = intent.colors()
    return when (style) {
        DPIconButtonStyle.Standard ->
            IconButtonDefaults.iconToggleButtonColors(
                containerColor = Color.Transparent,
                contentColor = c.accent,
                checkedContainerColor = c.accent,
                checkedContentColor = c.onAccent,
            )
        DPIconButtonStyle.Filled ->
            IconButtonDefaults.filledIconToggleButtonColors(
                containerColor = Color.Transparent,
                contentColor = c.accent,
                checkedContainerColor = c.accent,
                checkedContentColor = c.onAccent,
            )
        DPIconButtonStyle.Tonal ->
            IconButtonDefaults.filledTonalIconToggleButtonColors(
                containerColor = c.container,
                contentColor = c.onContainer,
                checkedContainerColor = c.accent,
                checkedContentColor = c.onAccent,
            )
        DPIconButtonStyle.Outlined ->
            IconButtonDefaults.outlinedIconToggleButtonColors(
                containerColor = Color.Transparent,
                contentColor = c.accent,
                checkedContainerColor = c.accent,
                checkedContentColor = c.onAccent,
            )
    }
}

@Composable
private fun dpIconToggleBorder(
    style: DPIconButtonStyle,
    enabled: Boolean,
    checked: Boolean,
    border: BorderStroke?,
): BorderStroke? {
    if (border != null) return border
    if (style != DPIconButtonStyle.Outlined) return null
    return IconButtonDefaults.outlinedIconToggleButtonBorder(enabled, checked)
}

@Composable
fun DPIconButton(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    style: DPIconButtonStyle = DPIconButtonStyle.Standard,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    enabled: Boolean = true,
    colors: IconButtonColors? = null,
    shape: Shape? = null,
    border: BorderStroke? = null,
    onClick: () -> Unit,
) {
    val resolvedColors = colors ?: dpIconButtonColors(style, intent)
    val resolvedShape = shape ?: dpIconButtonDefaultShape(style)
    val borderResolved = dpIconButtonBorder(style, intent, border)
    val m = modifier.dpIconButtonBox(size)
    when (style) {
        DPIconButtonStyle.Standard ->
            IconButton(
                onClick = onClick,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                content = { IconImage(icon, contentDescription, size) },
            )
        DPIconButtonStyle.Filled ->
            FilledIconButton(
                onClick = onClick,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                content = { IconImage(icon, contentDescription, size) },
            )
        DPIconButtonStyle.Tonal ->
            FilledTonalIconButton(
                onClick = onClick,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                content = { IconImage(icon, contentDescription, size) },
            )
        DPIconButtonStyle.Outlined ->
            OutlinedIconButton(
                onClick = onClick,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                border = borderResolved,
                content = { IconImage(icon, contentDescription, size) },
            )
    }
}

@Composable
private fun IconImage(
    icon: ImageVector,
    contentDescription: String?,
    size: DPSize,
) {
    val dim = size.iconSize()
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = Modifier.size(dim),
    )
}

@Composable
fun DPIconToggleButton(
    icon: ImageVector,
    checkedIcon: ImageVector = icon,
    contentDescription: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    style: DPIconButtonStyle = DPIconButtonStyle.Standard,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    enabled: Boolean = true,
    colors: IconToggleButtonColors? = null,
    shape: Shape? = null,
    border: BorderStroke? = null,
) {
    val resolvedColors = colors ?: dpIconToggleButtonColors(style, intent)
    val resolvedShape = shape ?: dpIconButtonDefaultShape(style)
    val borderResolved = dpIconToggleBorder(style, enabled, checked, border)
    val m = modifier.dpIconButtonBox(size)
    when (style) {
        DPIconButtonStyle.Standard ->
            IconToggleButton(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                content = { ToggleIconImage(icon, checkedIcon, contentDescription, checked, size) },
            )
        DPIconButtonStyle.Filled ->
            FilledIconToggleButton(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                content = { ToggleIconImage(icon, checkedIcon, contentDescription, checked, size) },
            )
        DPIconButtonStyle.Tonal ->
            FilledTonalIconToggleButton(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                content = { ToggleIconImage(icon, checkedIcon, contentDescription, checked, size) },
            )
        DPIconButtonStyle.Outlined ->
            OutlinedIconToggleButton(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = m,
                enabled = enabled,
                colors = resolvedColors,
                shape = resolvedShape,
                border = borderResolved,
                content = { ToggleIconImage(icon, checkedIcon, contentDescription, checked, size) },
            )
    }
}

@Composable
private fun ToggleIconImage(
    icon: ImageVector,
    checkedIcon: ImageVector,
    contentDescription: String?,
    checked: Boolean,
    size: DPSize,
) {
    val dim = size.iconSize()
    Icon(
        imageVector = if (checked) checkedIcon else icon,
        contentDescription = contentDescription,
        modifier = Modifier.size(dim),
    )
}

@DPComponentPreview
@Composable
private fun DPIconButtonVariantsPreview() {
    Preview {
        Row(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPIconButtonStyle.entries.forEach { st ->
                DPIconButton(
                    icon = Icons.Default.Favorite,
                    contentDescription = null,
                    onClick = {},
                    style = st,
                    intent = DPIntent.Primary,
                )
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPIconButtonMatrixPreview() {
    Preview {
        Row(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            listOf(DPIntent.Primary, DPIntent.Success, DPIntent.Danger).forEach { intent ->
                DPIconButton(
                    icon = Icons.Default.Favorite,
                    contentDescription = null,
                    onClick = {},
                    style = DPIconButtonStyle.Filled,
                    intent = intent,
                )
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPIconButtonSizePreview() {
    Preview {
        Row(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPSize.entries.forEach { s ->
                DPIconButton(
                    icon = Icons.Default.Favorite,
                    contentDescription = null,
                    onClick = {},
                    size = s,
                )
            }
        }
    }
}
