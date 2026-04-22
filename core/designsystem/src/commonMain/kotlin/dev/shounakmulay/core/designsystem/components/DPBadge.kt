package dev.shounakmulay.core.designsystem.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.*

enum class DPBadgeKind { Dot, Count, Status, Category, Label }

/**
 * Unified design-system badge. Replaces the legacy Material 3 `Badge` wrapper
 * and the removed `DPStatusBadge` / chip variants.
 *
 * **Migration from `DPStatusBadgeVariant` to [DPIntent]:**
 * - Healthy → [DPIntent.Success]
 * - Degraded → [DPIntent.Warning]
 * - Outage → [DPIntent.Danger]
 * - Info → [DPIntent.Info]
 * - Waiting → [DPIntent.Neutral]
 * - Neutral → [DPIntent.Neutral]
 * - New → [DPIntent.Primary]
 */
@Composable
fun DPBadge(
    modifier: Modifier = Modifier,
    text: String? = null,
    kind: DPBadgeKind = DPBadgeKind.Status,
    intent: DPIntent = DPIntent.Neutral,
    selected: Boolean = false,
    categoryKey: String? = null,
    onClick: (() -> Unit)? = null,
) {
    when (kind) {
        DPBadgeKind.Dot -> {
            val c = intent.colors()
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
            val c = intent.colors()
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
                DPMono(
                    text = label,
                    size = DPTextSize.Small,
                    color = c.onContainer,
                )
            }
        }

        DPBadgeKind.Status -> {
            if (text == null) return
            val c = intent.colors()
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
                DPMono(
                    text = text,
                    size = DPTextSize.Small,
                    color = c.onContainer,
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
                DPMono(
                    text = text.orEmpty(),
                    size = DPTextSize.Small,
                    color = catColor,
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
                DPLabel(
                    text = text.orEmpty(),
                    size = DPTextSize.Small,
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
    intent: DPIntent = DPIntent.Danger,
) {
    if (count <= 0) return
    val label = if (count > 99) "99+" else count.toString()
    DPBadge(modifier = modifier, text = label, kind = DPBadgeKind.Count, intent = intent)
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
                listOf(
                    DPIntent.Success,
                    DPIntent.Warning,
                    DPIntent.Danger,
                    DPIntent.Info,
                    DPIntent.Neutral,
                ).forEach { intent ->
                    DPBadge(text = intent.name, kind = DPBadgeKind.Status, intent = intent)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm)) {
                DPCountBadge(count = 3)
                DPCountBadge(count = 42, intent = DPIntent.Warning)
                DPCountBadge(count = 120, intent = DPIntent.Success)
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
