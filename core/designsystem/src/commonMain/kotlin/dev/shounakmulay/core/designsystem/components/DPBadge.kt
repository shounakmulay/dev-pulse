package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.categoryColour
import dev.shounakmulay.core.designsystem.theme.info
import dev.shounakmulay.core.designsystem.theme.infoContainer
import dev.shounakmulay.core.designsystem.theme.onInfoContainer
import dev.shounakmulay.core.designsystem.theme.onSuccessContainer
import dev.shounakmulay.core.designsystem.theme.onWarningContainer
import dev.shounakmulay.core.designsystem.theme.success
import dev.shounakmulay.core.designsystem.theme.successContainer
import dev.shounakmulay.core.designsystem.theme.warning
import dev.shounakmulay.core.designsystem.theme.warningContainer

enum class DPBadgeVariant {
    Healthy,
    Degraded,
    Outage,
    Info,
    Waiting,
    Neutral,
    New,
}

private data class BadgeColours(
    val surface: Color,
    val text: Color,
    val dot: Color,
    val border: Color,
)

@Composable
private fun badgeColours(variant: DPBadgeVariant): BadgeColours {
    val colorScheme = MaterialTheme.colorScheme
    return when (variant) {
        DPBadgeVariant.Healthy -> BadgeColours(
            colorScheme.successContainer,
            colorScheme.onSuccessContainer,
            colorScheme.onSuccessContainer,
            colorScheme.success
        )

        DPBadgeVariant.Degraded -> BadgeColours(
            colorScheme.warningContainer,
            colorScheme.onWarningContainer,
            colorScheme.onWarningContainer,
            colorScheme.warning,
        )

        DPBadgeVariant.Outage -> BadgeColours(
            colorScheme.errorContainer,
            colorScheme.onErrorContainer,
            colorScheme.onErrorContainer,
            colorScheme.error,
        )

        DPBadgeVariant.Info,
        DPBadgeVariant.New -> BadgeColours(
            colorScheme.infoContainer,
            colorScheme.onInfoContainer,
            colorScheme.onInfoContainer,
            colorScheme.info,
        )

        DPBadgeVariant.Waiting -> BadgeColours(
            colorScheme.secondaryContainer,
            colorScheme.onSecondaryContainer,
            colorScheme.onSecondaryContainer,
            colorScheme.secondary,
        )

        DPBadgeVariant.Neutral -> BadgeColours(
            colorScheme.surfaceContainerHigh,
            colorScheme.onSurfaceVariant,
            colorScheme.outline,
            colorScheme.outlineVariant,
        )
    }
}

@Composable
fun DPStatusBadge(
    variant: DPBadgeVariant,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colours = badgeColours(variant)
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(100.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(colours.surface, shape)
            .border(1.dp, colours.border, shape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(colours.dot),
        )
        Spacer(Modifier.width(4.dp))
        DPMono(
            text = label,
            size = DPTextSize.Small,
            color = colours.text,
        )
    }
}

@Composable
fun DPCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    variant: DPBadgeVariant = DPBadgeVariant.Waiting,
) {
    if (count <= 0) return

    val colours = badgeColours(variant)
    val shape = RoundedCornerShape(100.dp)
    val label = if (count > 99) "99+" else count.toString()

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
            .clip(shape)
            .background(colours.surface, shape)
            .border(1.dp, colours.border, shape)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        DPMono(
            text = label,
            size = DPTextSize.Small,
            color = colours.text,
        )
    }
}

@Composable
fun DPCategoryChip(
    category: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val catColour = categoryColour(category)
    val shape = RoundedCornerShape(4.dp)

    val bgAlpha = if (isSelected) 0.20f else 0.10f
    val borderAlpha = if (isSelected) 0.40f else 0.25f

    Row(
        modifier = modifier
            .clip(shape)
            .background(catColour.copy(alpha = bgAlpha), shape)
            .border(1.dp, catColour.copy(alpha = borderAlpha), shape)
            .then(
                if (onClick != null)
                    Modifier.clickable(
                        indication = ripple(),
                        interactionSource = null,
                        onClick = onClick
                    )
                else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(catColour),
        )
        Spacer(Modifier.width(4.dp))
        DPMono(
            text = category,
            size = DPTextSize.Small,
            color = catColour,
        )
    }
}

@Composable
fun DPLabelChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(4.dp)

    val bg = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceContainerHigh
    val border = if (isSelected) colorScheme.primary else colorScheme.outlineVariant
    val textColor = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, border, shape)
            .then(
                if (onClick != null)
                    Modifier.clickable(
                        indication = ripple(),
                        interactionSource = null,
                        onClick = onClick
                    )
                else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        DPLabel(
            text = label,
            size = DPTextSize.Small,
            color = textColor,
        )
    }
}

@DPComponentPreview
@Composable
private fun DPStatusBadgePreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DPBadgeVariant.entries.forEach { variant ->
                DPStatusBadge(variant = variant, label = variant.name)
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPCountBadgePreview() {
    Preview {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DPCountBadge(count = 5)
            DPCountBadge(count = 10, variant = DPBadgeVariant.Healthy)
            DPCountBadge(count = 99)
            DPCountBadge(count = 120, variant = DPBadgeVariant.Outage)
        }
    }
}

@DPComponentPreview
@Composable
private fun DPCategoryChipPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DPCategoryChip(category = "Android")
            DPCategoryChip(category = "Kotlin", isSelected = true)
            DPCategoryChip(category = "Compose")
        }
    }
}

@DPComponentPreview
@Composable
private fun DPLabelChipPreview() {
    Preview {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DPLabelChip(label = "Default")
            DPLabelChip(label = "Selected", isSelected = true)
        }
    }
}
