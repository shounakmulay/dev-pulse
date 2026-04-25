package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
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
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    density: DPDensity = DPDensity.Default,
    enabled: Boolean = true,
) {
    val hPad = DPTheme.spacing.screenHorizontal
    val vPad = density.verticalPadding()
    val iconSize = DPTheme.iconSize.md

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.38f)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = hPad, vertical = vPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize).padding(end = DPTheme.spacing.sm),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            DPTextView(text = title, variant = DPTextViewVariant.BodyMedium)
            if (subtitle != null) {
                DPTextView(
                    text = subtitle,
                    variant = DPTextViewVariant.LabelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (trailingIcon != null) {
            if (onTrailingIconClick != null) {
                IconButton(onClick = onTrailingIconClick) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                    )
                }
            } else {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// Slot-based overload
@Composable
fun DPClickableRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    density: DPDensity = DPDensity.Default,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val hPad = DPTheme.spacing.screenHorizontal
    val vPad = density.verticalPadding()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.38f)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = hPad, vertical = vPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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
            DPClickableRow(title = "Dense", subtitle = "Dense density", onClick = {}, density = DPDensity.Dense)
            DPClickableRow(title = "Disabled", onClick = {}, enabled = false)
        }
    }
}
