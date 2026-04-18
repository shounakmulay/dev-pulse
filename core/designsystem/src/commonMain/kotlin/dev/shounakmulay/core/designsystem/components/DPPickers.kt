package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPDatePicker(
    state: DatePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: DatePickerFormatter = remember { DatePickerDefaults.dateFormatter() },
    colors: DatePickerColors = DatePickerDefaults.colors(),
    title: (@Composable () -> Unit)? = {
        DatePickerDefaults.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)),
            contentColor = colors.titleContentColor,
        )
    },
    headline: (@Composable () -> Unit)? = {
        DatePickerDefaults.DatePickerHeadline(
            selectedDateMillis = state.selectedDateMillis,
            displayMode = state.displayMode,
            dateFormatter = dateFormatter,
            modifier = Modifier.padding(PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)),
            contentColor = colors.headlineContentColor,
        )
    },
    showModeToggle: Boolean = true,
    focusRequester: FocusRequester? = remember { FocusRequester() },
) {
    DatePicker(
        state = state,
        modifier = modifier,
        dateFormatter = dateFormatter,
        colors = colors,
        title = title,
        headline = headline,
        showModeToggle = showModeToggle,
        focusRequester = focusRequester,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPDateRangePicker(
    state: DateRangePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: DatePickerFormatter = remember { DatePickerDefaults.dateFormatter() },
    colors: DatePickerColors = DatePickerDefaults.colors(),
    title: (@Composable () -> Unit)? = {
        DateRangePickerDefaults.DateRangePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(PaddingValues(start = 64.dp, end = 12.dp)),
            contentColor = colors.titleContentColor,
        )
    },
    headline: (@Composable () -> Unit)? = {
        DateRangePickerDefaults.DateRangePickerHeadline(
            selectedStartDateMillis = state.selectedStartDateMillis,
            selectedEndDateMillis = state.selectedEndDateMillis,
            displayMode = state.displayMode,
            dateFormatter,
            modifier = Modifier.padding(PaddingValues(start = 64.dp, end = 12.dp, bottom = 12.dp)),
            contentColor = colors.headlineContentColor,
        )
    },
    showModeToggle: Boolean = true,
    focusRequester: FocusRequester? = remember { FocusRequester() },
) {
    DateRangePicker(
        state = state,
        modifier = modifier,
        dateFormatter = dateFormatter,
        colors = colors,
        title = title,
        headline = headline,
        showModeToggle = showModeToggle,
        focusRequester = focusRequester,
    )
}

@Composable
fun DPDatePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    shape: Shape = DatePickerDefaults.shape,
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    colors: DatePickerColors = DatePickerDefaults.colors(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit,
) {
//    DatePickerDialog(
//        onDismissRequest = onDismissRequest,
//        confirmButton = confirmButton,
//        modifier = modifier,
//        dismissButton = dismissButton,
//        shape = shape,
//        tonalElevation = tonalElevation,
//        colors = colors,
//        properties = properties,
//        content = content,
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPTimePicker(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    colors: TimePickerColors = TimePickerDefaults.colors(),
    layoutType: TimePickerLayoutType = TimePickerDefaults.layoutType(),
) {
    TimePicker(
        state = state,
        modifier = modifier,
        colors = colors,
        layoutType = layoutType,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPTimeInput(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    colors: TimePickerColors = TimePickerDefaults.colors(),
) {
    TimeInput(
        state = state,
        modifier = modifier,
        colors = colors,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPPickersPreview() {
    Preview {
        val dateState =
            rememberDatePickerState(
                initialDisplayMode = DisplayMode.Picker,
            )
        DPDatePicker(state = dateState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPTimePickersPreview() {
    Preview {
        DPTimePicker(state = rememberTimePickerState())
        DPTimeInput(state = rememberTimePickerState(initialHour = 1, initialMinute = 30))
    }
}
