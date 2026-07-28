package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.kizitonwose.calendar.core.now
import dev.shounakmulay.devpulse.core.common.time.toLocalDateTime
import dev.shounakmulay.devpulse.core.designsystem.components.DPDateRangePickerDialog
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.OptionChip
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange.PublishedRangeFilterViewModel.Companion.publishedRangeTextResource
import devpulse.core.resources.generated.resources.published_date
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Clock

@Composable
fun LazyItemScope.PublishedRangeFilter(
    filter: RssPostFilter.PublishedRange,
    onFilterUpdated: (RssPostFilter.PublishedRange) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val bottomSheetController =
        rememberDPModalBottomSheetController(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    PublishedRangeFilter(
        filter = filter,
        bottomSheetController = bottomSheetController,
        showDatePicker = showDatePicker,
        onDismissDatePicker = { showDatePicker = false },
        onShowDatePicker = { showDatePicker = true },
        onFilterUpdated = {
            onFilterUpdated(it)
            coroutineScope.launch { bottomSheetController.hide() }
        },
        getPublishedRangeText = {
            publishedRangeTextResource(it)
        }
    )
}

@Composable
private fun LazyItemScope.PublishedRangeFilter(
    filter: RssPostFilter.PublishedRange,
    bottomSheetController: DPModalBottomSheetController,
    showDatePicker: Boolean,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onFilterUpdated: (RssPostFilter.PublishedRange) -> Unit,
    getPublishedRangeText: (RssPostFilter.PublishedRange) -> TextResource?
) {

    if (showDatePicker) {
        val state = rememberDateRangePickerState(
            yearRange = 1970..LocalDate.now().year,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
                }
            }
        )
        val coroutineScope = rememberCoroutineScope()
        DPDateRangePickerDialog(
            onDismissRequest = onDismissDatePicker,
            state = state,
            onDateRangeSelected = { min, max ->
                onFilterUpdated(
                    filter.copy(
                        min = min.toLocalDateTime(),
                        max = max.toLocalDateTime()
                    )
                )
            },
            onDateRangeCleared = {
                onFilterUpdated(filter.copy(min = null, max = null))
                coroutineScope.launch { bottomSheetController.hide() }
            },
        )
    }

    PublishedRangeOptionBottomSheet(
        bottomSheetController = bottomSheetController,
        onShowDatePicker = onShowDatePicker,
        onFilterUpdated = onFilterUpdated,
        filter = filter
    )

    val label = remember(filter) {
        getPublishedRangeText(filter)
            ?: TextResource.fromStringRes(stringRes.published_date)
    }
    OptionChip(
        onClick = { bottomSheetController.show() },
        label = label.asString(),
        selected = filter.min != null && filter.max != null
    )
}



