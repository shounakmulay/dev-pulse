package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheetController
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import devpulse.core.resources.generated.resources.select_feeds
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun FeedFilterFeedsBottomSheet(
    feedListBottomSheetController: DPModalBottomSheetController,
    filter: RssPostFilter.FeedIds,
    onFilterUpdated: (RssPostFilter) -> Unit
) {
    DPModalBottomSheet(controller = feedListBottomSheetController) {
        val scopedViewModelOwner = rememberViewModelStoreOwner()
        CompositionLocalProvider(LocalViewModelStoreOwner provides scopedViewModelOwner) {
            val viewModel = koinViewModel<FeedListBottomSheetViewModel> {
                parametersOf(filter.values)
            }
            val feeds = viewModel.feeds.collectAsLazyPagingItems()
            DPTextView(
                modifier = Modifier.padding(
                    horizontal = LocalDPSpacing.current.xl,
                    vertical = LocalDPSpacing.current.xs
                ),
                text = stringResource(stringRes.select_feeds),
                variant = DPTextViewVariant.TitleMedium
            )
            FeedsList(feeds = feeds, onEvent = viewModel::onEvent)
            FilterApplyActions(
                onFilterUpdated = onFilterUpdated,
                filter = filter,
                feedListBottomSheetController = feedListBottomSheetController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun FeedsList(
    feeds: LazyPagingItems<Pair<UIFeed, Boolean>>,
    onEvent: (FeedListBottomSheetEvent) -> Unit,
) {
    BoxWithConstraints {
        val maxHeight = maxHeight * 0.75f
        LazyColumn(
            modifier = Modifier.heightIn(max = maxHeight),
        ) {
            items(feeds.itemCount, feeds.itemKey { it.first.id }) {
                val (feed, selected) = feeds[it] ?: return@items
                FeedListItem(onEvent, feed, selected)
            }
        }
    }
}