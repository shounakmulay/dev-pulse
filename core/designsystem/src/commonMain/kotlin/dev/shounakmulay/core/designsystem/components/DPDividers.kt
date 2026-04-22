package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.DPIntent
import dev.shounakmulay.core.designsystem.theme.DPTheme
import dev.shounakmulay.core.designsystem.theme.colors

enum class DPDividerVariant { Hairline, Section, Emphasized }

@Composable
@ReadOnlyComposable
private fun resolvedDividerThickness(variant: DPDividerVariant): Dp = when (variant) {
    DPDividerVariant.Hairline -> DividerDefaults.Thickness
    DPDividerVariant.Section -> 1.dp
    DPDividerVariant.Emphasized -> 2.dp
}

@Composable
@ReadOnlyComposable
private fun resolvedDividerColor(variant: DPDividerVariant, intent: DPIntent): Color {
    val cs = MaterialTheme.colorScheme
    return if (intent == DPIntent.Neutral) {
        when (variant) {
            DPDividerVariant.Hairline -> cs.outlineVariant
            DPDividerVariant.Section -> cs.outline
            DPDividerVariant.Emphasized -> cs.outline
        }
    } else {
        val c = intent.colors()
        when (variant) {
            DPDividerVariant.Hairline -> c.outline.copy(alpha = 0.5f)
            DPDividerVariant.Section -> c.outline
            DPDividerVariant.Emphasized -> c.accent
        }
    }
}

@Composable
fun DPHorizontalDivider(
    modifier: Modifier = Modifier,
    variant: DPDividerVariant = DPDividerVariant.Hairline,
    intent: DPIntent = DPIntent.Neutral,
    thickness: Dp? = null,
    color: Color? = null,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness ?: resolvedDividerThickness(variant),
        color = color ?: resolvedDividerColor(variant, intent),
    )
}

@Composable
fun DPVerticalDivider(
    modifier: Modifier = Modifier,
    variant: DPDividerVariant = DPDividerVariant.Hairline,
    intent: DPIntent = DPIntent.Neutral,
    thickness: Dp? = null,
    color: Color? = null,
) {
    VerticalDivider(
        modifier = modifier,
        thickness = thickness ?: resolvedDividerThickness(variant),
        color = color ?: resolvedDividerColor(variant, intent),
    )
}

@DPComponentPreview
@Composable
private fun DPDividersPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPDividerVariant.entries.forEach { v ->
                DPHorizontalDivider(modifier = Modifier.fillMaxWidth(), variant = v)
            }
            DPHorizontalDivider(modifier = Modifier.fillMaxWidth(), variant = DPDividerVariant.Section, intent = DPIntent.Primary)
            DPHorizontalDivider(modifier = Modifier.fillMaxWidth(), variant = DPDividerVariant.Emphasized, intent = DPIntent.Danger)
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).padding(top = DPTheme.spacing.sm),
            ) {
                DPVerticalDivider(modifier = Modifier.fillMaxHeight())
                DPVerticalDivider(modifier = Modifier.fillMaxHeight().padding(start = DPTheme.spacing.md), variant = DPDividerVariant.Emphasized, intent = DPIntent.Success)
            }
        }
    }
}
