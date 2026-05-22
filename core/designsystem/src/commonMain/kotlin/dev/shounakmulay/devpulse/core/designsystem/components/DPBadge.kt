package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryColour
import dev.shounakmulay.devpulse.core.designsystem.theme.monoFontFamily

enum class DPBadgeKind { Dot, Count, Status, Category, Label }

enum class DPStatusVariant { Primary, Secondary, Tertiary, Error, Neutral }

private data class BadgeColors(
    val container: Color,
    val onContainer: Color,
    val accent: Color,
    val outline: Color,
)

@Composable
@ReadOnlyComposable
private fun DPStatusVariant.colors(): BadgeColors {
    val cs = MaterialTheme.colorScheme
    return when (this) {
        DPStatusVariant.Primary -> BadgeColors(cs.primaryContainer, cs.onPrimaryContainer, cs.primary, cs.primary)
        DPStatusVariant.Secondary -> BadgeColors(cs.secondaryContainer, cs.onSecondaryContainer, cs.secondary, cs.secondary)
        DPStatusVariant.Tertiary -> BadgeColors(cs.tertiaryContainer, cs.onTertiaryContainer, cs.tertiary, cs.tertiary)
        DPStatusVariant.Error -> BadgeColors(cs.errorContainer, cs.onErrorContainer, cs.error, cs.error)
        DPStatusVariant.Neutral -> BadgeColors(cs.surfaceContainerHigh, cs.onSurface, cs.outline, cs.outlineVariant)
    }
}

@Composable
fun DPBadge(
    modifier: Modifier = Modifier,
    text: String? = null,
    kind: DPBadgeKind = DPBadgeKind.Status,
    variant: DPStatusVariant = DPStatusVariant.Neutral,
    selected: Boolean = false,
    categoryKey: String? = null,
    onClick: (() -> Unit)? = null,
) {
    when (kind) {
        DPBadgeKind.Dot -> {
            val c = variant.colors()
            Box(
                modifier = modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(c.accent, CircleShape),
            )
        }

        DPBadgeKind.Count -> {
            val label = text ?: return
            if (label != "99+") {
                val n = label.toIntOrNull() ?: return
                if (n <= 0) return
            }
            val c = variant.colors()
            val shape = RoundedCornerShape(100.dp)
            Box(
                modifier = modifier
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .clip(shape)
                    .background(c.container, shape)
                    .border(BorderStroke(1.dp, c.outline), shape)
                    .padding(horizontal = 5.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                DPTextView(
                    text = label,
                    variant = DPTextViewVariant.LabelSmall,
                    color = c.onContainer,
                    fontFamily = monoFontFamily(),
                )
            }
        }

        DPBadgeKind.Status -> {
            if (text == null) return
            val c = variant.colors()
            val shape = RoundedCornerShape(100.dp)
            Row(
                modifier = modifier
                    .clip(shape)
                    .background(c.container, shape)
                    .border(BorderStroke(1.dp, c.outline), shape)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(c.accent),
                )
                Spacer(Modifier.width(4.dp))
                DPTextView(
                    text = text,
                    variant = DPTextViewVariant.LabelSmall,
                    color = c.onContainer,
                    fontFamily = monoFontFamily(),
                )
            }
        }

        DPBadgeKind.Category -> {
            val key = categoryKey ?: text.orEmpty()
            val catColor = categoryColour(key)
            val shape = RoundedCornerShape(4.dp)
            val bgAlpha = if (selected) 0.20f else 0.10f
            val borderAlpha = if (selected) 0.40f else 0.25f
            Row(
                modifier = modifier
                    .clip(shape)
                    .background(catColor.copy(alpha = bgAlpha), shape)
                    .border(1.dp, catColor.copy(alpha = borderAlpha), shape)
                    .then(
                        if (onClick != null) {
                            Modifier.clickable(
                                indication = ripple(),
                                interactionSource = null,
                                onClick = onClick,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(catColor),
                )
                Spacer(Modifier.width(4.dp))
                DPTextView(
                    text = text.orEmpty(),
                    variant = DPTextViewVariant.LabelSmall,
                    color = catColor,
                    fontFamily = monoFontFamily(),
                )
            }
        }

        DPBadgeKind.Label -> {
            val colorScheme = MaterialTheme.colorScheme
            val shape = RoundedCornerShape(4.dp)
            val bg = if (selected) colorScheme.primaryContainer else colorScheme.surfaceContainerHigh
            val borderColor = if (selected) colorScheme.primary else colorScheme.outlineVariant
            val textColor = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant
            Box(
                modifier = modifier
                    .clip(shape)
                    .background(bg, shape)
                    .border(1.dp, borderColor, shape)
                    .then(
                        if (onClick != null) {
                            Modifier.clickable(
                                indication = ripple(),
                                interactionSource = null,
                                onClick = onClick,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center,
            ) {
                DPTextView(
                    text = text.orEmpty(),
                    variant = DPTextViewVariant.LabelSmall,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
fun DPCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    variant: DPStatusVariant = DPStatusVariant.Error,
) {
    if (count <= 0) return
    val label = if (count > 99) "99+" else count.toString()
    DPBadge(modifier = modifier, text = label, kind = DPBadgeKind.Count, variant = variant)
}

@DPComponentPreview
@Composable
private fun DPBadgeKindsPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                DPStatusVariant.entries.forEach { variant ->
                    DPBadge(text = variant.name, kind = DPBadgeKind.Status, variant = variant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                DPCountBadge(count = 3)
                DPCountBadge(count = 42, variant = DPStatusVariant.Secondary)
                DPCountBadge(count = 120, variant = DPStatusVariant.Tertiary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                DPBadge(text = "Default", kind = DPBadgeKind.Label)
                DPBadge(text = "Selected", kind = DPBadgeKind.Label, selected = true)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                DPBadge(text = "Kotlin", kind = DPBadgeKind.Category, categoryKey = "Kotlin")
                DPBadge(
                    text = "Android",
                    kind = DPBadgeKind.Category,
                    categoryKey = "Android",
                    selected = true,
                )
            }
        }
    }
}
