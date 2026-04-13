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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.foundation.categoryColour
import dev.shounakmulay.core.designsystem.theme.DPTheme

// ─────────────────────────────────────────────────────────────────────────────
// BADGE VARIANTS
// ─────────────────────────────────────────────────────────────────────────────

enum class DPBadgeVariant {
    Healthy,    // Operational / success — jade
    Degraded,   // Warning / partial — amber
    Outage,     // Critical error — crimson
    Info,       // New version, informational — sapphire
    Waiting,    // Pending / queued — indigo
    Neutral,    // Inactive / general
    New,        // New item badge (alias for Info)
}

private data class BadgeColours(
    val surface: Color,
    val text: Color,
    val dot: Color,
    val border: Color,
)

@Composable
private fun badgeColours(variant: DPBadgeVariant): BadgeColours {
    val c = DPTheme.colours
    return when (variant) {
        DPBadgeVariant.Healthy -> BadgeColours(
            c.signalSuccessSurface,
            c.signalSuccessText,
            c.signalSuccessText,
            c.signalSuccessBorder
        )

        DPBadgeVariant.Degraded -> BadgeColours(
            c.signalWarningSurface,
            c.signalWarningText,
            c.accentPrimary,
            c.signalWarningBorder
        )

        DPBadgeVariant.Outage -> BadgeColours(
            c.signalErrorSurface,
            c.signalErrorText,
            c.signalErrorText,
            c.signalErrorBorder
        )

        DPBadgeVariant.Info,
        DPBadgeVariant.New -> BadgeColours(
            c.signalInfoSurface,
            c.signalInfoText,
            c.signalInfoText,
            c.signalInfoBorder
        )

        DPBadgeVariant.Waiting -> BadgeColours(
            c.signalWaitingSurface,
            c.signalWaitingText,
            c.signalWaitingText,
            c.signalWaitingBorder
        )

        DPBadgeVariant.Neutral -> BadgeColours(
            c.backgroundSubtle,
            c.textSecondary,
            c.textTertiary,
            c.borderDefault
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DPSTATUSBADGE — semantic status pill with dot indicator
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPStatusBadge(
    variant: DPBadgeVariant,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colours = badgeColours(variant)
    val typography = DPTheme.typography
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

// ─────────────────────────────────────────────────────────────────────────────
// DPCOUNTBADGE — unread count indicator; hidden when count == 0
// ─────────────────────────────────────────────────────────────────────────────

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

// ─────────────────────────────────────────────────────────────────────────────
// DPCATEGORYCHIP — source category identity mark
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPCategoryChip(
    category: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colours = DPTheme.colours
    val catColour = colours.categoryColour(category)
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

// ─────────────────────────────────────────────────────────────────────────────
// DPLABELCHIP — generic text chip with accent styling
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPLabelChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colours = DPTheme.colours
    val shape = RoundedCornerShape(4.dp)

    val bg = if (isSelected) colours.accentPrimarySubtle else colours.backgroundInteractive
    val border = if (isSelected) colours.accentPrimaryBorder else colours.borderDefault
    val textColor = if (isSelected) colours.accentPrimary else colours.textSecondary

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

// ─────────────────────────────────────────────────────────────────────────────
// PREVIEWS
// ─────────────────────────────────────────────────────────────────────────────

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
