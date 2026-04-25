package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.MultiChoiceSegmentedButtonRowScope
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPIntent
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.colors

// --- Slot-based scope extensions (original API) ---

@Composable
fun SingleChoiceSegmentedButtonRowScope.DPSegmentedButton(
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SegmentedButtonColors = SegmentedButtonDefaults.colors(),
    contentPadding: PaddingValues = SegmentedButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    icon: @Composable () -> Unit = { SegmentedButtonDefaults.Icon(selected) },
    label: @Composable () -> Unit,
) {
    SegmentedButton(
        selected = selected, onClick = onClick, shape = shape, modifier = modifier,
        enabled = enabled, colors = colors, contentPadding = contentPadding,
        interactionSource = interactionSource, icon = icon, label = label,
    )
}

@Composable
fun MultiChoiceSegmentedButtonRowScope.DPSegmentedButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SegmentedButtonColors = SegmentedButtonDefaults.colors(),
    contentPadding: PaddingValues = SegmentedButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    icon: @Composable () -> Unit = { SegmentedButtonDefaults.Icon(checked) },
    label: @Composable () -> Unit,
) {
    SegmentedButton(
        checked = checked, onCheckedChange = onCheckedChange, shape = shape, modifier = modifier,
        enabled = enabled, colors = colors, contentPadding = contentPadding,
        interactionSource = interactionSource, icon = icon, label = label,
    )
}

// --- Text-first scope extensions (new variant-aware API) ---

@Composable
private fun resolveSegmentedColors(intent: DPIntent, colors: SegmentedButtonColors?): SegmentedButtonColors {
    if (colors != null) return colors
    if (intent == DPIntent.Primary) return SegmentedButtonDefaults.colors()
    val c = intent.colors()
    return SegmentedButtonDefaults.colors(
        activeBorderColor = c.outline,
        inactiveBorderColor = c.outline.copy(alpha = 0.38f),
        activeContainerColor = c.container,
        activeContentColor = c.onContainer,
    )
}

@Composable
fun SingleChoiceSegmentedButtonRowScope.DPSegmentedButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    intent: DPIntent = DPIntent.Primary,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: SegmentedButtonColors? = null,
) {
    val resolvedColors = resolveSegmentedColors(intent, colors)
    DPSegmentedButton(
        selected = selected, onClick = onClick, shape = shape, modifier = modifier,
        enabled = enabled, colors = resolvedColors,
        icon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else {
            { SegmentedButtonDefaults.Icon(selected) }
        },
        label = { DPTextView(text = text, variant = DPTextViewVariant.LabelMedium) },
    )
}

@Composable
fun MultiChoiceSegmentedButtonRowScope.DPSegmentedButton(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    intent: DPIntent = DPIntent.Primary,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: SegmentedButtonColors? = null,
) {
    val resolvedColors = resolveSegmentedColors(intent, colors)
    DPSegmentedButton(
        checked = checked, onCheckedChange = onCheckedChange, shape = shape, modifier = modifier,
        enabled = enabled, colors = resolvedColors,
        icon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else {
            { SegmentedButtonDefaults.Icon(checked) }
        },
        label = { DPTextView(text = text, variant = DPTextViewVariant.LabelMedium) },
    )
}

@DPComponentPreview
@Composable
private fun DPSegmentedButtonPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
        ) {
            var single by remember { mutableIntStateOf(0) }
            SingleChoiceSegmentedButtonRow {
                listOf("One", "Two", "Three").forEachIndexed { index, label ->
                    DPSegmentedButton(
                        text = label,
                        selected = single == index,
                        onClick = { single = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    )
                }
            }
            var multiFirst by remember { mutableStateOf(false) }
            var multiSecond by remember { mutableStateOf(true) }
            MultiChoiceSegmentedButtonRow {
                DPSegmentedButton(
                    text = "A", checked = multiFirst, onCheckedChange = { multiFirst = it },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    intent = DPIntent.Success,
                )
                DPSegmentedButton(
                    text = "B", checked = multiSecond, onCheckedChange = { multiSecond = it },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    intent = DPIntent.Success,
                )
            }
        }
    }
}
