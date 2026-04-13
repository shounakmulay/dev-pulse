package dev.shounakmulay.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.foundation.DPMotion
import dev.shounakmulay.core.designsystem.theme.DPTheme

// ─────────────────────────────────────────────────────────────────────────────
// BUTTON VARIANTS & SIZES
// ─────────────────────────────────────────────────────────────────────────────

enum class DPButtonVariant { Primary, Secondary, Ghost, Destructive, Caution }

enum class DPButtonSize {
    Small,   // 36dp height, 12dp padding
    Medium,  // 44dp height, 16dp padding
    Large,   // 52dp height, 20dp padding
}

private data class DPButtonSizeTokens(
    val height: Dp,
    val horizontalPadding: Dp,
)

private fun sizeTokens(size: DPButtonSize) = when (size) {
    DPButtonSize.Small  -> DPButtonSizeTokens(height = 36.dp, horizontalPadding = 12.dp)
    DPButtonSize.Medium -> DPButtonSizeTokens(height = 44.dp, horizontalPadding = 16.dp)
    DPButtonSize.Large  -> DPButtonSizeTokens(height = 52.dp, horizontalPadding = 20.dp)
}

// ─────────────────────────────────────────────────────────────────────────────
// DPBUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DPButtonVariant = DPButtonVariant.Primary,
    size: DPButtonSize = DPButtonSize.Medium,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val shape = DPTheme.shape

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius = when (size) {
        DPButtonSize.Small  -> shape.xs
        DPButtonSize.Medium -> shape.sm
        DPButtonSize.Large  -> shape.sm
    }
    val sizeT = sizeTokens(size)

    val (baseFill, baseText, baseBorder) = when {
        !enabled -> Triple(
            colours.backgroundInteractive,
            colours.textDisabled,
            Color.Transparent,
        )
        variant == DPButtonVariant.Primary -> Triple(
            colours.interactivePrimaryFill,
            colours.interactivePrimaryText,
            Color.Transparent,
        )
        variant == DPButtonVariant.Secondary -> Triple(
            colours.interactiveSecondaryFill,
            colours.textPrimary,
            colours.interactiveSecondaryBorder,
        )
        variant == DPButtonVariant.Ghost -> Triple(
            Color.Transparent,
            colours.textSecondary,
            Color.Transparent,
        )
        variant == DPButtonVariant.Destructive -> Triple(
            colours.interactiveDestructiveFill,
            colours.interactiveDestructiveText,
            Color.Transparent,
        )
        else -> Triple(
            colours.signalWarningSurface,
            colours.signalWarningText,
            colours.signalWarningBorder,
        )
    }

    val pressedFill = when {
        !enabled -> baseFill
        variant == DPButtonVariant.Primary -> colours.interactivePrimaryPressed
        else -> baseFill.copy(alpha = (baseFill.alpha * 0.85f).coerceIn(0f, 1f))
    }

    val fillColour by animateColorAsState(
        targetValue = if (isPressed && enabled) pressedFill else baseFill,
        animationSpec = DPMotion.microSpec(),
        label = "dp_button_fill",
    )
    val textColour by animateColorAsState(
        targetValue = baseText,
        animationSpec = DPMotion.microSpec(),
        label = "dp_button_text",
    )

    val textStyle = when (size) {
        DPButtonSize.Small  -> typography.labelMedium
        DPButtonSize.Medium -> typography.labelLarge
        DPButtonSize.Large  -> typography.labelLarge
    }

    val shape2d = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = sizeT.height)
            .clip(shape2d)
            .background(fillColour, shape2d)
            .then(
                if (baseBorder != Color.Transparent)
                    border(BorderStroke(1.dp, baseBorder), shape2d)
                else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled && !isLoading,
                onClick = onClick,
            )
            .padding(horizontal = sizeT.horizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = textColour,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColour,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
            }
            DPLabel(
                text = label,
                style = textStyle,
                color = textColour,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DPICONBUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    enabled: Boolean = true,
    size: DPButtonSize = DPButtonSize.Medium,
) {
    val colours = DPTheme.colours
    val shape = DPTheme.shape

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val iconTint = tint ?: colours.textSecondary
    val pressedTint = colours.textPrimary

    val resolvedTint by animateColorAsState(
        targetValue = if (isPressed && enabled) pressedTint else if (enabled) iconTint else colours.textDisabled,
        animationSpec = DPMotion.microSpec(),
        label = "dp_icon_btn_tint",
    )

    val touchSize = when (size) {
        DPButtonSize.Small  -> 36.dp
        DPButtonSize.Medium -> 44.dp
        DPButtonSize.Large  -> 48.dp
    }
    val iconSize = when (size) {
        DPButtonSize.Small  -> 16.dp
        DPButtonSize.Medium -> 20.dp
        DPButtonSize.Large  -> 24.dp
    }

    Box(
        modifier = modifier
            .size(touchSize)
            .clip(RoundedCornerShape(shape.sm))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = resolvedTint,
            modifier = Modifier.size(iconSize),
        )
    }
}
