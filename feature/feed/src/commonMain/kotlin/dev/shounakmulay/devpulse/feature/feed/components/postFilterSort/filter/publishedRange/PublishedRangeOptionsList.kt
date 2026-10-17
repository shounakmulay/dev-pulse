package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter

@Composable
internal fun ColumnScope.PublishedRangeOptionsList(
    state: PublishedRangeFilterState,
    onShowDatePicker: () -> Unit,
    onFilterUpdated: (UIPublishedRange) -> Unit,
    filter: RssPostFilter.PublishedRange
) {
    LazyColumn(Modifier.weight(1f, fill = false)) {
        items(
            state.rangeValues,
            key = {
                it.toString()
            }
        ) { publishedRange ->
            PublishedRangeOptionRow(
                publishedRange = publishedRange,
                onShowDatePicker = onShowDatePicker,
                onFilterUpdated = onFilterUpdated,
                state = state,
                filter = filter
            )
        }
    }
}

@DPScreenPreview
@Composable
private fun PublishedRangeOptionsListPreview() {
    Preview {
        val state = PublishedRangeFilterState(
            selectedRange = UIPublishedRange.Last7Days,
            rangeValues = listOf(UIPublishedRange.Today, UIPublishedRange.Last24Hours, UIPublishedRange.Last7Days, UIPublishedRange.Last30Days, UIPublishedRange.ThisMonth, UIPublishedRange.Custom(null, null)),
        )
        Column {
            PublishedRangeOptionsList(
                state = state,
                onShowDatePicker = {},
                onFilterUpdated = {},
                filter = RssPostFilter.PublishedRange(),
            )
        }
    }
}
