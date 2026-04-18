package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.MultiChoiceSegmentedButtonRowScope
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPSingleChoiceSegmentedButtonRow(
    modifier: Modifier = Modifier,
    space: Dp = SegmentedButtonDefaults.BorderWidth,
    content: @Composable SingleChoiceSegmentedButtonRowScope.() -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
        space = space,
        content = content,
    )
}

@Composable
fun DPMultiChoiceSegmentedButtonRow(
    modifier: Modifier = Modifier,
    space: Dp = SegmentedButtonDefaults.BorderWidth,
    content: @Composable MultiChoiceSegmentedButtonRowScope.() -> Unit,
) {
    MultiChoiceSegmentedButtonRow(
        modifier = modifier,
        space = space,
        content = content,
    )
}

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
        selected = selected,
        onClick = onClick,
        shape = shape,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        icon = icon,
        label = label,
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
        checked = checked,
        onCheckedChange = onCheckedChange,
        shape = shape,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        icon = icon,
        label = label,
    )
}

@DPComponentPreview
@Composable
private fun DPSegmentedButtonPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var single by remember { mutableIntStateOf(0) }
            DPSingleChoiceSegmentedButtonRow {
                DPSegmentedButton(
                    selected = single == 0,
                    onClick = { single = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                    label = { Text("One") },
                )
                DPSegmentedButton(
                    selected = single == 1,
                    onClick = { single = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                    label = { Text("Two") },
                )
                DPSegmentedButton(
                    selected = single == 2,
                    onClick = { single = 2 },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    label = { Text("Three") },
                )
            }
            var multiFirst by remember { mutableStateOf(false) }
            var multiSecond by remember { mutableStateOf(true) }
            DPMultiChoiceSegmentedButtonRow {
                DPSegmentedButton(
                    checked = multiFirst,
                    onCheckedChange = { multiFirst = it },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    label = { Text("A") },
                )
                DPSegmentedButton(
                    checked = multiSecond,
                    onCheckedChange = { multiSecond = it },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = { Text("B") },
                )
            }
        }
    }
}
