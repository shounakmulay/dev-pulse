package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.annotation.IntRange
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpPrimaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpSecondaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpTertiaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.minHeight

enum class DPSelectionVariant { Primary, Secondary, Tertiary }

@Composable
@ReadOnlyComposable
private fun DPSelectionVariant.colors(): DPVariantColors = when (this) {
    DPSelectionVariant.Primary -> dpPrimaryVariantColors()
    DPSelectionVariant.Secondary -> dpSecondaryVariantColors()
    DPSelectionVariant.Tertiary -> dpTertiaryVariantColors()
}

@Composable
fun DPCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: DPSelectionVariant = DPSelectionVariant.Primary,
    size: DPSize = DPSize.Medium,
    colors: CheckboxColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = variant.colors()
    val resolved = colors ?: CheckboxDefaults.colors(
        checkedColor = c.accent,
        checkmarkColor = c.onAccent,
        uncheckedColor = c.outline,
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
    variant: DPSelectionVariant = DPSelectionVariant.Primary,
    size: DPSize = DPSize.Medium,
    colors: CheckboxColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = variant.colors()
    val resolved = colors ?: CheckboxDefaults.colors(
        checkedColor = c.accent,
        checkmarkColor = c.onAccent,
        uncheckedColor = c.outline,
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
    variant: DPSelectionVariant = DPSelectionVariant.Primary,
    size: DPSize = DPSize.Medium,
    colors: RadioButtonColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = variant.colors()
    val resolved = colors ?: RadioButtonDefaults.colors(
        selectedColor = c.accent,
        unselectedColor = c.outline,
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
    variant: DPSelectionVariant = DPSelectionVariant.Primary,
    size: DPSize = DPSize.Medium,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = variant.colors()
    val resolved = colors ?: SwitchDefaults.colors(
        checkedThumbColor = c.onAccent,
        checkedTrackColor = c.accent,
        checkedBorderColor = c.accent,
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
    variant: DPSelectionVariant = DPSelectionVariant.Primary,
    size: DPSize = DPSize.Medium,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val c = variant.colors()
    val resolved = colors ?: SliderDefaults.colors(
        thumbColor = c.accent,
        activeTrackColor = c.accent,
        inactiveTrackColor = c.container,
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
    variant: DPSelectionVariant = DPSelectionVariant.Primary,
    size: DPSize = DPSize.Medium,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors? = null,
) {
    val c = variant.colors()
    val resolved = colors ?: SliderDefaults.colors(
        thumbColor = c.accent,
        activeTrackColor = c.accent,
        inactiveTrackColor = c.container,
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

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSelectionVariant.entries.forEach { variant ->
                    DPCheckbox(checked = checked, onCheckedChange = { checked = it }, variant = variant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSelectionVariant.entries.forEach { variant ->
                    DPTriStateCheckbox(state = tri, onClick = { tri = tri.next() }, variant = variant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                DPSelectionVariant.entries.forEachIndexed { i, variant ->
                    DPRadioButton(selected = radio == i, onClick = { radio = i }, variant = variant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSelectionVariant.entries.forEach { variant ->
                    DPSwitch(checked = switched, onCheckedChange = { switched = it }, variant = variant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSelectionVariant.entries.forEach { variant ->
                    DPSlider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        variant = variant,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSelectionVariant.entries.forEach { variant ->
                    DPRangeSlider(
                        value = range,
                        onValueChange = { range = it },
                        variant = variant,
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
