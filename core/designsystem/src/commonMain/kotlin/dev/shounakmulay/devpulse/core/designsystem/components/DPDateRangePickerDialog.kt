package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.apply_filter
import devpulse.core.resources.generated.resources.clear
import org.jetbrains.compose.resources.stringResource

@Composable
fun DPDateRangePickerDialog(
    modifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState(),
    onDismissRequest: () -> Unit,
    onDateRangeSelected: (Long, Long) -> Unit,
    onDateRangeCleared: () -> Unit,
) {
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DPButton(
                text = stringResource(stringRes.apply_filter),
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                variant = DPButtonVariant.Tertiary
            ) {
                val min = state.selectedStartDateMillis
                val max = state.selectedEndDateMillis
                if (min != null && max != null) {
                    onDateRangeSelected(min, max)
                    onDismissRequest()
                }
            }
        },
        dismissButton = {
            DPButton(
                text = stringResource(stringRes.clear),
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                variant = DPButtonVariant.Tertiary,
                style = DPButtonStyle.Text
            ) {
                state.setSelection(null, null)
                onDateRangeCleared()
                onDismissRequest()
            }
        }
    ) {
        val datePickerFormatter = remember { DatePickerDefaults.dateFormatter() }
        DateRangePicker(
            state = state,
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = state.displayMode,
                    modifier = Modifier
                        .padding(horizontal = LocalDPSpacing.current.lg)
                        .padding(top = LocalDPSpacing.current.lg),
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = state.selectedStartDateMillis,
                    selectedEndDateMillis = state.selectedEndDateMillis,
                    displayMode = state.displayMode,
                    dateFormatter = datePickerFormatter,
                    modifier = Modifier.padding(12.dp),
                )
            },
            colors = DatePickerDefaults.colors(
                selectedDayContentColor = MaterialTheme.colorScheme.onTertiary,
                selectedDayContainerColor = MaterialTheme.colorScheme.tertiary,
                dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onTertiary,
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                todayContentColor = MaterialTheme.colorScheme.tertiary,
                todayDateBorderColor = MaterialTheme.colorScheme.tertiary,
                selectedYearContentColor = MaterialTheme.colorScheme.onTertiary,
                selectedYearContainerColor = MaterialTheme.colorScheme.tertiary,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                yearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                dateTextFieldColors = TextFieldDefaults.colors(
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.tertiary,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    selectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.tertiary,
                        backgroundColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                    )
                )
            ),
        )
    }
}