package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.annotation.IntRange
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPIntent
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.minHeight
import dev.shounakmulay.devpulse.core.designsystem.theme.colors

@Composable
fun DPCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    colors: CheckboxColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val resolved = colors ?: CheckboxDefaults.colors(
        checkedColor = intent.colors().accent,
        checkmarkColor = intent.colors().onAccent,
        uncheckedColor = intent.colors().outline,
    )
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(size.minHeight()),
        enabled = enabled,
        colors = resolved,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    colors: CheckboxColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val resolved = colors ?: CheckboxDefaults.colors(
        checkedColor = intent.colors().accent,
        checkmarkColor = intent.colors().onAccent,
        uncheckedColor = intent.colors().outline,
    )
    TriStateCheckbox(
        state = state,
        onClick = onClick,
        modifier = modifier.size(size.minHeight()),
        enabled = enabled,
        colors = resolved,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    colors: RadioButtonColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val resolved = colors ?: RadioButtonDefaults.colors(
        selectedColor = intent.colors().accent,
        unselectedColor = intent.colors().outline,
    )
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier.size(size.minHeight()),
        enabled = enabled,
        colors = resolved,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val resolved = colors ?: SwitchDefaults.colors(
        checkedThumbColor = intent.colors().onAccent,
        checkedTrackColor = intent.colors().accent,
        checkedBorderColor = intent.colors().accent,
    )
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(size.minHeight()),
        thumbContent = thumbContent,
        enabled = enabled,
        colors = resolved,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val resolved = colors ?: SliderDefaults.colors(
        thumbColor = intent.colors().accent,
        activeTrackColor = intent.colors().accent,
        inactiveTrackColor = intent.colors().containerVariant,
    )
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(size.minHeight()),
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = resolved,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors? = null,
) {
    val resolved = colors ?: SliderDefaults.colors(
        thumbColor = intent.colors().accent,
        activeTrackColor = intent.colors().accent,
        inactiveTrackColor = intent.colors().containerVariant,
    )
    RangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(size.minHeight()),
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = resolved,
    )
}

@DPComponentPreview
@Composable
private fun DPSelectionControlsPreview() {
    Preview {
        var checked by remember { mutableStateOf(true) }
        var tri by remember { mutableStateOf(ToggleableState.Indeterminate) }
        var radio by remember { mutableStateOf(0) }
        var switched by remember { mutableStateOf(false) }
        var sliderValue by remember { mutableStateOf(0.4f) }
        var range by remember { mutableStateOf(0.25f..0.75f) }
        val intentSamples = listOf(
            DPIntent.Primary,
            DPIntent.Success,
            DPIntent.Danger,
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                intentSamples.forEach { intent ->
                    DPCheckbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        intent = intent,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                intentSamples.forEach { intent ->
                    DPTriStateCheckbox(
                        state = tri,
                        onClick = { tri = tri.next() },
                        intent = intent,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                intentSamples.forEachIndexed { i, intent ->
                    DPRadioButton(
                        selected = radio == i,
                        onClick = { radio = i },
                        intent = intent,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                intentSamples.forEach { intent ->
                    DPSwitch(
                        checked = switched,
                        onCheckedChange = { switched = it },
                        intent = intent,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                intentSamples.forEach { intent ->
                    DPSlider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        intent = intent,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                intentSamples.forEach { intent ->
                    DPRangeSlider(
                        value = range,
                        onValueChange = { range = it },
                        intent = intent,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private fun ToggleableState.next(): ToggleableState =
    when (this) {
        ToggleableState.On -> ToggleableState.Off
        ToggleableState.Off -> ToggleableState.Indeterminate
        ToggleableState.Indeterminate -> ToggleableState.On
    }
