package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPDensity
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.verticalPadding

// Text-first primary API
@Composable
fun DPClickableRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingIconSize: Dp = DPTheme.iconSize.lg,
    leadingIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingIcon: ImageVector? = null,
    trailingIconSize: Dp = DPTheme.iconSize.lg,
    trailingIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onTrailingIconClick: (() -> Unit)? = null,
    density: DPDensity = DPDensity.Default,
    enabled: Boolean = true,
) {
    val hPad = DPTheme.spacing.screenHorizontal
    val vPad = density.verticalPadding()
    val minHeight = when (density) {
        DPDensity.Dense -> 48.dp
        DPDensity.Default -> 56.dp
        DPDensity.Comfortable -> 64.dp
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.38f)
            .padding(horizontal = hPad, vertical = vPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(leadingIconSize),
                tint = leadingIconTint,
            )
            Spacer(Modifier.width(DPTheme.spacing.sm))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.xs),
        ) {
            DPTextView(
                text = title,
                variant = DPTextViewVariant.BodyLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )
            if (subtitle != null) {
                DPTextView(
                    text = subtitle,
                    variant = DPTextViewVariant.BodySmall,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (trailingIcon != null) {
            Spacer(Modifier.width(DPTheme.spacing.sm))
            if (onTrailingIconClick != null) {
                IconButton(onClick = onTrailingIconClick, enabled = enabled) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(trailingIconSize),
                        tint = trailingIconTint,
                    )
                }
            } else {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(trailingIconSize),
                    tint = trailingIconTint,
                )
            }
        }
    }
}

// Slot-based overload
@Composable
fun DPClickableRow(
    onClick: () -> Unit,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    modifier: Modifier = Modifier,
    density: DPDensity = DPDensity.Default,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val hPad = DPTheme.spacing.screenHorizontal
    val vPad = density.verticalPadding()
    val minHeight = when (density) {
        DPDensity.Dense -> 48.dp
        DPDensity.Default -> 56.dp
        DPDensity.Comfortable -> 64.dp
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(MaterialTheme.shapes.large)
            .alpha(if (enabled) 1f else 0.38f)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = hPad, vertical = vPad),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        content = content,
    )
}

@DPComponentPreview
@Composable
private fun DPClickableRowPreview() {
    Preview {
        Column {
            DPClickableRow(title = "Default row", onClick = {})
            DPClickableRow(title = "With subtitle", subtitle = "Supporting text", onClick = {})
            DPClickableRow(
                title = "Dense",
                subtitle = "Dense density",
                onClick = {},
                density = DPDensity.Dense
            )
            DPClickableRow(title = "Disabled", onClick = {}, enabled = false)
        }
    }
}

@DPComponentPreview
@Composable
private fun DPClickableRowLargeTextPreview() {
    Preview {
        CompositionLocalProvider(
            LocalDensity provides Density(density = LocalDensity.current.density, fontScale = 2f),
        ) {
            Column(modifier = Modifier.width(320.dp)) {
                DPClickableRow(
                    title = "A long feed name that needs room to wrap",
                    subtitle = "Supporting information should remain readable at a larger text size.",
                    onClick = {},
                    leadingIcon = Icons.Default.Info,
                    leadingIconTint = MaterialTheme.colorScheme.primary,
                    trailingIcon = Icons.Default.Info,
                    trailingIconTint = MaterialTheme.colorScheme.tertiary,
                    onTrailingIconClick = {},
                )
                DPClickableRow(
                    title = "A disabled row with a long descriptive title",
                    subtitle = "This supporting text can also grow to multiple lines.",
                    onClick = {},
                    leadingIcon = Icons.Default.Info,
                    enabled = false,
                )
            }
        }
    }
}
