package dev.shounakmulay.devpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.ui.theme.DPTheme

// ─────────────────────────────────────────────────────────────────────────────
// CARD VARIANTS
// ─────────────────────────────────────────────────────────────────────────────

enum class DPCardVariant {
    Default,    // Standard card — elevated surface with subtle border
    Outlined,   // Selected / focused state — stronger border
    Flat,       // List context — no elevation, hairline border
    Ghost,      // Secondary content — low-opacity fill
}

// ─────────────────────────────────────────────────────────────────────────────
// DPCARD — Column layout card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPCard(
    modifier: Modifier = Modifier,
    variant: DPCardVariant = DPCardVariant.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colours = DPTheme.colours
    val shape = DPTheme.shape

    val cornerRadius = shape.md
    val shape2d = RoundedCornerShape(cornerRadius)

    val (bg, border) = when (variant) {
        DPCardVariant.Default  -> Pair(colours.backgroundElevated, colours.borderDefault)
        DPCardVariant.Outlined -> Pair(colours.backgroundElevated, colours.borderStrong)
        DPCardVariant.Flat     -> Pair(colours.backgroundElevated, colours.borderSubtle)
        DPCardVariant.Ghost    -> Pair(colours.backgroundOverlay.copy(alpha = 0.4f), colours.borderSubtle)
    }

    Column(
        modifier = modifier
            .clip(shape2d)
            .background(bg, shape2d)
            .border(1.dp, border, shape2d)
            .then(
                if (onClick != null)
                    Modifier.clickable(indication = ripple(), interactionSource = null, onClick = onClick)
                else Modifier
            )
            .padding(DPTheme.spacing.lg),
        content = content,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// DPSURFACE — Box layout surface (for custom content placement)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPSurface(
    modifier: Modifier = Modifier,
    variant: DPCardVariant = DPCardVariant.Default,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colours = DPTheme.colours
    val shape = DPTheme.shape

    val shape2d = RoundedCornerShape(shape.md)

    val (bg, border) = when (variant) {
        DPCardVariant.Default  -> Pair(colours.backgroundElevated, colours.borderDefault)
        DPCardVariant.Outlined -> Pair(colours.backgroundElevated, colours.borderStrong)
        DPCardVariant.Flat     -> Pair(colours.backgroundElevated, colours.borderSubtle)
        DPCardVariant.Ghost    -> Pair(colours.backgroundOverlay.copy(alpha = 0.4f), colours.borderSubtle)
    }

    Box(
        modifier = modifier
            .clip(shape2d)
            .background(bg, shape2d)
            .border(1.dp, border, shape2d)
            .then(
                if (onClick != null)
                    Modifier.clickable(indication = ripple(), interactionSource = null, onClick = onClick)
                else Modifier
            ),
        content = { content() },
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// DPSECTION — labelled content group
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPSection(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = DPTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        DPSectionLabel(
            text = label,
            modifier = Modifier.padding(
                start = spacing.screenHorizontal,
                bottom = spacing.sm,
            ),
        )
        content()
    }
}
