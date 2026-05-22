@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupMenuState
import androidx.compose.material3.ButtonGroupScope
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.DPVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.contentPadding
import dev.shounakmulay.devpulse.core.designsystem.theme.dpPrimaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpSecondaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpTertiaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.iconLabelGap
import dev.shounakmulay.devpulse.core.designsystem.theme.iconSize
import dev.shounakmulay.devpulse.core.designsystem.theme.labelStyle
import dev.shounakmulay.devpulse.core.designsystem.theme.minHeight

enum class DPButtonStyle { Filled, Tonal, Outlined, Text, Elevated }

enum class DPButtonVariant { Primary, Secondary, Tertiary }

@Composable
@ReadOnlyComposable
private fun DPButtonVariant.colors(): DPVariantColors = when (this) {
    DPButtonVariant.Primary -> dpPrimaryVariantColors()
    DPButtonVariant.Secondary -> dpSecondaryVariantColors()
    DPButtonVariant.Tertiary -> dpTertiaryVariantColors()
}

@Composable
private fun resolveButtonColors(style: DPButtonStyle, variant: DPButtonVariant): ButtonColors {
    val c = variant.colors()
    return when (style) {
        DPButtonStyle.Filled -> ButtonDefaults.buttonColors(
            containerColor = c.accent,
            contentColor = c.onAccent,
            disabledContainerColor = c.accent.copy(alpha = 0.12f),
            disabledContentColor = c.onAccent.copy(alpha = 0.38f),
        )
        DPButtonStyle.Tonal -> ButtonDefaults.filledTonalButtonColors(
            containerColor = c.container,
            contentColor = c.onContainer,
            disabledContainerColor = c.container.copy(alpha = 0.12f),
            disabledContentColor = c.onContainer.copy(alpha = 0.38f),
        )
        DPButtonStyle.Outlined -> ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = c.accent,
            disabledContentColor = c.accent.copy(alpha = 0.38f),
        )
        DPButtonStyle.Text -> ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = c.accent,
            disabledContentColor = c.accent.copy(alpha = 0.38f),
        )
        DPButtonStyle.Elevated -> ButtonDefaults.elevatedButtonColors(
            containerColor = c.container,
            contentColor = c.onContainer,
            disabledContainerColor = c.container.copy(alpha = 0.12f),
            disabledContentColor = c.onContainer.copy(alpha = 0.38f),
        )
    }
}

@Composable
private fun resolveButtonShape(style: DPButtonStyle): Shape = when (style) {
    DPButtonStyle.Filled -> ButtonDefaults.shape
    DPButtonStyle.Tonal -> ButtonDefaults.filledTonalShape
    DPButtonStyle.Outlined -> ButtonDefaults.outlinedShape
    DPButtonStyle.Text -> ButtonDefaults.textShape
    DPButtonStyle.Elevated -> ButtonDefaults.elevatedShape
}

@Composable
@ReadOnlyComposable
private fun resolveButtonBorder(
    style: DPButtonStyle,
    variant: DPButtonVariant,
    enabled: Boolean,
): BorderStroke? =
    if (style == DPButtonStyle.Outlined) BorderStroke(
        1.dp,
        variant.colors().outline.copy(alpha = if (enabled) 1f else 0.38f),
    )
    else null

@Composable
fun DPButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DPButtonStyle = DPButtonStyle.Filled,
    variant: DPButtonVariant = DPButtonVariant.Primary,
    size: DPSize = DPSize.Medium,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: ButtonColors? = null,
    shape: Shape? = null,
    contentPadding: PaddingValues? = null,
    border: BorderStroke? = null,
    elevation: ButtonElevation? = null,
) {
    DPButton(
        onClick = onClick,
        modifier = modifier,
        style = style,
        variant = variant,
        size = size,
        enabled = enabled,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        border = border,
        elevation = elevation,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(size.iconSize()),
            )
            Spacer(Modifier.width(size.iconLabelGap()))
        }
        Text(text = text, style = size.labelStyle())
        if (trailingIcon != null) {
            Spacer(Modifier.width(size.iconLabelGap()))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(size.iconSize()),
            )
        }
    }
}

@Composable
fun DPButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DPButtonStyle = DPButtonStyle.Filled,
    variant: DPButtonVariant = DPButtonVariant.Primary,
    size: DPSize = DPSize.Medium,
    enabled: Boolean = true,
    colors: ButtonColors? = null,
    shape: Shape? = null,
    contentPadding: PaddingValues? = null,
    border: BorderStroke? = null,
    elevation: ButtonElevation? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val resolvedColors = colors ?: resolveButtonColors(style, variant)
    val resolvedShape = shape ?: resolveButtonShape(style)
    val resolvedPadding = contentPadding ?: size.contentPadding()
    val resolvedBorder = border ?: resolveButtonBorder(style, variant, enabled)
    val heightMod = modifier.heightIn(min = size.minHeight())

    when (style) {
        DPButtonStyle.Filled -> Button(
            onClick = onClick, modifier = heightMod, enabled = enabled,
            shape = resolvedShape, colors = resolvedColors,
            elevation = elevation ?: ButtonDefaults.buttonElevation(),
            border = resolvedBorder, contentPadding = resolvedPadding, content = content,
        )
        DPButtonStyle.Tonal -> FilledTonalButton(
            onClick = onClick, modifier = heightMod, enabled = enabled,
            shape = resolvedShape, colors = resolvedColors,
            elevation = elevation ?: ButtonDefaults.filledTonalButtonElevation(),
            border = resolvedBorder, contentPadding = resolvedPadding, content = content,
        )
        DPButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick, modifier = heightMod, enabled = enabled,
            shape = resolvedShape, colors = resolvedColors,
            elevation = elevation, border = resolvedBorder,
            contentPadding = resolvedPadding, content = content,
        )
        DPButtonStyle.Text -> TextButton(
            onClick = onClick, modifier = heightMod, enabled = enabled,
            shape = resolvedShape, colors = resolvedColors,
            elevation = elevation, border = resolvedBorder,
            contentPadding = resolvedPadding, content = content,
        )
        DPButtonStyle.Elevated -> ElevatedButton(
            onClick = onClick, modifier = heightMod, enabled = enabled,
            shape = resolvedShape, colors = resolvedColors,
            elevation = elevation ?: ButtonDefaults.elevatedButtonElevation(),
            border = resolvedBorder, contentPadding = resolvedPadding, content = content,
        )
    }
}

@Composable
fun DPButtonGroup(
    overflowIndicator: @Composable (ButtonGroupMenuState) -> Unit,
    modifier: Modifier = Modifier,
    expandedRatio: Float = ButtonGroupDefaults.ExpandedRatio,
    horizontalArrangement: Arrangement.Horizontal = ButtonGroupDefaults.HorizontalArrangement,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: ButtonGroupScope.() -> Unit,
) {
    ButtonGroup(
        overflowIndicator = overflowIndicator,
        modifier = modifier,
        expandedRatio = expandedRatio,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
    )
}

@Composable
fun DPSplitButton(
    leadingButton: @Composable () -> Unit,
    trailingButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = SplitButtonDefaults.Spacing,
) {
    SplitButtonLayout(
        leadingButton = leadingButton,
        trailingButton = trailingButton,
        modifier = modifier,
        spacing = spacing,
    )
}

@DPComponentPreview
@Composable
private fun DPButtonMatrixPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPButtonStyle.entries.forEach { style ->
                Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                    DPButtonVariant.entries.forEach { variant ->
                        DPButton(text = style.name, onClick = {}, style = style, variant = variant)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                DPSize.entries.forEach { s ->
                    DPButton(text = s.name, onClick = {}, size = s)
                }
            }
            DPButton(text = "With Icon", onClick = {}, leadingIcon = Icons.Default.Add)
            DPButton(text = "Disabled", onClick = {}, enabled = false)
        }
    }
}
