package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPElevationLevel
import dev.shounakmulay.devpulse.core.designsystem.theme.DPIntent
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.colors
import dev.shounakmulay.devpulse.core.designsystem.theme.value

enum class DPCardStyle { Filled, Elevated, Outlined }

@Composable
private fun resolveCardColors(style: DPCardStyle, intent: DPIntent): CardColors {
    if (intent == DPIntent.Neutral) {
        return when (style) {
            DPCardStyle.Filled -> CardDefaults.cardColors()
            DPCardStyle.Elevated -> CardDefaults.elevatedCardColors()
            DPCardStyle.Outlined -> CardDefaults.outlinedCardColors()
        }
    }
    val c = intent.colors()
    return when (style) {
        DPCardStyle.Filled -> CardDefaults.cardColors(
            containerColor = c.container,
            contentColor = c.onContainer,
        )
        DPCardStyle.Elevated -> CardDefaults.elevatedCardColors(
            containerColor = c.container,
            contentColor = c.onContainer,
        )
        DPCardStyle.Outlined -> CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
            contentColor = c.onContainer,
        )
    }
}

@Composable
private fun resolveCardBorder(style: DPCardStyle, intent: DPIntent, enabled: Boolean): BorderStroke? {
    if (style != DPCardStyle.Outlined) return null
    return if (intent == DPIntent.Neutral) {
        CardDefaults.outlinedCardBorder(enabled)
    } else {
        BorderStroke(1.dp, intent.colors().outline)
    }
}

@Composable
fun DPCard(
    modifier: Modifier = Modifier,
    style: DPCardStyle = DPCardStyle.Filled,
    intent: DPIntent = DPIntent.Neutral,
    elevation: DPElevationLevel = DPElevationLevel.Level1,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape? = null,
    colors: CardColors? = null,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val resolvedColors = colors ?: resolveCardColors(style, intent)
    val resolvedBorder = border ?: resolveCardBorder(style, intent, enabled)
    val elevationDp = elevation.value()

    when (style) {
        DPCardStyle.Filled -> {
            val resolvedShape = shape ?: CardDefaults.shape
            if (onClick != null) {
                Card(
                    onClick = onClick, modifier = modifier, enabled = enabled,
                    shape = resolvedShape, colors = resolvedColors,
                    elevation = CardDefaults.cardElevation(),
                    border = resolvedBorder, interactionSource = null, content = content,
                )
            } else {
                Card(
                    modifier = modifier, shape = resolvedShape, colors = resolvedColors,
                    elevation = CardDefaults.cardElevation(), border = resolvedBorder, content = content,
                )
            }
        }
        DPCardStyle.Elevated -> {
            val resolvedShape = shape ?: CardDefaults.elevatedShape
            if (onClick != null) {
                ElevatedCard(
                    onClick = onClick, modifier = modifier, enabled = enabled,
                    shape = resolvedShape, colors = resolvedColors,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevationDp),
                    interactionSource = null, content = content,
                )
            } else {
                ElevatedCard(
                    modifier = modifier, shape = resolvedShape, colors = resolvedColors,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevationDp),
                    content = content,
                )
            }
        }
        DPCardStyle.Outlined -> {
            val resolvedShape = shape ?: CardDefaults.outlinedShape
            if (onClick != null) {
                OutlinedCard(
                    onClick = onClick, modifier = modifier, enabled = enabled,
                    shape = resolvedShape, colors = resolvedColors,
                    elevation = CardDefaults.outlinedCardElevation(),
                    border = resolvedBorder ?: CardDefaults.outlinedCardBorder(enabled),
                    interactionSource = null, content = content,
                )
            } else {
                OutlinedCard(
                    modifier = modifier, shape = resolvedShape, colors = resolvedColors,
                    elevation = CardDefaults.outlinedCardElevation(),
                    border = resolvedBorder ?: CardDefaults.outlinedCardBorder(),
                    content = content,
                )
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPCardVariantsPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPCardStyle.entries.forEach { style ->
                DPCard(style = style, onClick = {}) {
                    DPTextView(text = "$style card", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(DPTheme.spacing.md))
                }
            }
            DPCard(style = DPCardStyle.Filled, intent = DPIntent.Success) {
                DPTextView(text = "Success intent", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(DPTheme.spacing.md))
            }
            DPCard(style = DPCardStyle.Outlined, intent = DPIntent.Danger) {
                DPTextView(text = "Danger outlined", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(DPTheme.spacing.md))
            }
        }
    }
}
