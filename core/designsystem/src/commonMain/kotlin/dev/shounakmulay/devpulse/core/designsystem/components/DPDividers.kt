package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

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
private fun resolvedDividerColor(variant: DPDividerVariant): Color {
    val cs = MaterialTheme.colorScheme
    return when (variant) {
        DPDividerVariant.Hairline -> cs.outlineVariant
        DPDividerVariant.Section -> cs.outline
        DPDividerVariant.Emphasized -> cs.outline
    }
}

@Composable
fun DPHorizontalDivider(
    modifier: Modifier = Modifier,
    variant: DPDividerVariant = DPDividerVariant.Hairline,
    thickness: Dp? = null,
    color: Color? = null,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness ?: resolvedDividerThickness(variant),
        color = color ?: resolvedDividerColor(variant),
    )
}

@Composable
fun DPVerticalDivider(
    modifier: Modifier = Modifier,
    variant: DPDividerVariant = DPDividerVariant.Hairline,
    thickness: Dp? = null,
    color: Color? = null,
) {
    VerticalDivider(
        modifier = modifier,
        thickness = thickness ?: resolvedDividerThickness(variant),
        color = color ?: resolvedDividerColor(variant),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPSectionDivider(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val spacing = LocalDPSpacing.current
    Surface(
        shape = MaterialTheme.shapes.extraLargeIncreased.copy(
            topStart = CornerSize(0.dp),
            topEnd = CornerSize(0.dp)
        ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = modifier
                .padding(
                    start = spacing.xl,
                    end = spacing.sm
                )
                .padding(vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs, alignment = Alignment.End)
        ) {
            DPTextView(
                text = title.uppercase(),
                variant = DPTextViewVariant.TitleSmallEmphasized
            )
            DPHorizontalDivider(Modifier.padding(spacing.sm).weight(1f))
            actions()
        }
    }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = DPTheme.spacing.sm),
            ) {
                DPVerticalDivider(modifier = Modifier.fillMaxHeight())
                DPVerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = DPTheme.spacing.md),
                    variant = DPDividerVariant.Emphasized,
                )
            }
        }
    }
}
