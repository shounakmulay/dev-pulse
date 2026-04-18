package dev.shounakmulay.core.designsystem.components

import androidx.annotation.IntRange
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.VerticalSlider
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@Composable
fun DPCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    checkmarkStroke: Stroke,
    outlineStroke: Stroke,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        checkmarkStroke = checkmarkStroke,
        outlineStroke = outlineStroke,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    TriStateCheckbox(
        state = state,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    checkmarkStroke: Stroke,
    outlineStroke: Stroke,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    TriStateCheckbox(
        state = state,
        onClick = onClick,
        checkmarkStroke = checkmarkStroke,
        outlineStroke = outlineStroke,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
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
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = thumbContent,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0,
    thumb: @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderDefaults.Track(colors = colors, enabled = enabled, sliderState = sliderState)
    },
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        steps = steps,
        thumb = thumb,
        track = track,
        valueRange = valueRange,
    )
}

@Composable
fun DPSlider(
    state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderDefaults.Track(colors = colors, enabled = enabled, sliderState = sliderState)
    },
) {
    Slider(
        state = state,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}

@Composable
fun DPRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
) {
    RangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
    )
}

@Composable
fun DPRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    startThumb: @Composable (RangeSliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = startInteractionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    endThumb: @Composable (RangeSliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = endInteractionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (RangeSliderState) -> Unit = { rangeSliderState ->
        SliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            rangeSliderState = rangeSliderState,
        )
    },
    @IntRange(from = 0) steps: Int = 0,
) {
    RangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = startThumb,
        endThumb = endThumb,
        track = track,
        steps = steps,
    )
}

@Composable
fun DPRangeSlider(
    state: RangeSliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(),
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    startThumb: @Composable (RangeSliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = startInteractionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    endThumb: @Composable (RangeSliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = endInteractionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (RangeSliderState) -> Unit = { rangeSliderState ->
        SliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            rangeSliderState = rangeSliderState,
        )
    },
) {
    RangeSlider(
        state = state,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = startThumb,
        endThumb = endThumb,
        track = track,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPVerticalSlider(
    state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            sliderState = sliderState,
            trackCornerSize = Dp.Unspecified,
        )
    },
) {
    VerticalSlider(
        state = state,
        modifier = modifier,
        enabled = enabled,
        reverseDirection = reverseDirection,
        colors = colors,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
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
        val sliderState = rememberSliderState(0.5f)
        val rangeState = rememberRangeSliderState(0.2f, 0.8f)
        val verticalState = rememberSliderState(0.35f)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DPCheckbox(checked = checked, onCheckedChange = { checked = it })
            DPTriStateCheckbox(state = tri, onClick = { tri = tri.next() })
            DPRadioButton(selected = radio == 0, onClick = { radio = 0 })
            DPRadioButton(selected = radio == 1, onClick = { radio = 1 })
            DPSwitch(checked = switched, onCheckedChange = { switched = it })
            DPSlider(value = sliderValue, onValueChange = { sliderValue = it })
            DPRangeSlider(value = range, onValueChange = { range = it })
            DPSlider(state = sliderState)
            DPRangeSlider(state = rangeState)
            DPVerticalSlider(
                state = verticalState,
                modifier = Modifier.height(120.dp),
            )
        }
    }
}

private fun ToggleableState.next(): ToggleableState =
    when (this) {
        ToggleableState.On -> ToggleableState.Off
        ToggleableState.Off -> ToggleableState.Indeterminate
        ToggleableState.Indeterminate -> ToggleableState.On
    }
