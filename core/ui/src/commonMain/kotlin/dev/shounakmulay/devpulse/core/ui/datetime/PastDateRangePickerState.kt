package dev.shounakmulay.devpulse.core.ui.datetime

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun rememberPastDateRangePickerState(modifier: Modifier = Modifier): DateRangePickerState {
    return rememberDateRangePickerState(
        selectableDates = remember {
            object : SelectableDates {
                val localDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return Instant.fromEpochMilliseconds(utcTimeMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date <= localDate
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year <= localDate.year
                }
            }
        }
    )
}