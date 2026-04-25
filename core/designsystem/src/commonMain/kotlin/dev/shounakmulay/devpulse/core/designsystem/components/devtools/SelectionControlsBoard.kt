package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPCheckbox
import dev.shounakmulay.devpulse.core.designsystem.components.DPRadioButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPRangeSlider
import dev.shounakmulay.devpulse.core.designsystem.components.DPSlider
import dev.shounakmulay.devpulse.core.designsystem.components.DPSwitch
import dev.shounakmulay.devpulse.core.designsystem.components.DPTriStateCheckbox
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@Composable
fun SelectionControlsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPTextView(text = "DPCheckbox", variant = DPTextViewVariant.BodyMedium) }
        item {
            var checked by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                DPCheckbox(checked = checked, onCheckedChange = { checked = it })
                DPTextView(text = "Enabled", variant = DPTextViewVariant.BodyMedium)
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DPCheckbox(checked = true, onCheckedChange = null, enabled = false)
                DPTextView(text = "Disabled checked", variant = DPTextViewVariant.BodyMedium)
            }
        }

        item { DPTextView(text = "DPTriStateCheckbox", variant = DPTextViewVariant.BodyMedium) }
        item {
            var state by remember { mutableStateOf(ToggleableState.Indeterminate) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                DPTriStateCheckbox(
                    state = state,
                    onClick = {
                        state = when (state) {
                            ToggleableState.Off -> ToggleableState.Indeterminate
                            ToggleableState.Indeterminate -> ToggleableState.On
                            ToggleableState.On -> ToggleableState.Off
                        }
                    },
                )
                DPTextView(text = state.toString(), variant = DPTextViewVariant.BodyMedium)
            }
        }

        item { DPTextView(text = "DPRadioButton", variant = DPTextViewVariant.BodyMedium) }
        item {
            var selected by remember { mutableStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("A", "B", "C").forEachIndexed { index, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DPRadioButton(
                            selected = selected == index,
                            onClick = { selected = index },
                        )
                        DPTextView(text = label, variant = DPTextViewVariant.BodyMedium)
                    }
                }
            }
        }

        item { DPTextView(text = "DPSwitch", variant = DPTextViewVariant.BodyMedium) }
        item {
            var on by remember { mutableStateOf(true) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DPSwitch(checked = on, onCheckedChange = { on = it })
                DPTextView(text = if (on) "On" else "Off", variant = DPTextViewVariant.BodyMedium)
            }
        }
        item {
            DPSwitch(checked = false, onCheckedChange = null, enabled = false)
        }

        item { DPTextView(text = "DPSlider", variant = DPTextViewVariant.BodyMedium) }
        item {
            var value by remember { mutableStateOf(0.5f) }
            DPSlider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item { DPTextView(text = "DPRangeSlider", variant = DPTextViewVariant.BodyMedium) }
        item {
            var range by remember { mutableStateOf(0.2f..0.8f) }
            DPRangeSlider(
                value = range,
                onValueChange = { range = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
