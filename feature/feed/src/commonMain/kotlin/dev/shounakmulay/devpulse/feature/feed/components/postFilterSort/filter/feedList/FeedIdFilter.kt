package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.OptionChip
import devpulse.core.resources.generated.resources.feeds
import devpulse.core.resources.generated.resources.feeds_count

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LazyItemScope.FeedIdFilter(
    filter: RssPostFilter.FeedIds,
    onFilterUpdated: (RssPostFilter) -> Unit
) {
    val feedListBottomSheetController = rememberDPModalBottomSheetController(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    )
    FeedFilterFeedsBottomSheet(feedListBottomSheetController, filter, onFilterUpdated)

    val label = remember(filter) {
        if (filter.values.isEmpty()) {
            TextResource.fromStringRes(stringRes.feeds)
        } else {
            TextResource.fromStringResWithArgs(
                stringRes.feeds_count,
                filter.values.size.toString()
            )
        }
    }
    OptionChip(
        modifier = Modifier.animateItem(),
        selected = filter.values.isNotEmpty(),
        onClick = { feedListBottomSheetController.show() },
        label = label.asString()
    )
}







