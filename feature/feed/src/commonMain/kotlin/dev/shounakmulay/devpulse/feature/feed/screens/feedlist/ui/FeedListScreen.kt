package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendErrorRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendLoadingRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.EmptyFeedList
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListError
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListPlaceholderRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FilterTabs
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import devpulse.core.resources.generated.resources.add_feed_feeds
import devpulse.core.resources.generated.resources.feed_list_load_error
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedListScreen(
    viewModel: FeedListViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val feeds = viewModel.uiFeedsFlow.collectAsLazyPagingItems()
    val refreshState = feeds.loadState.refresh
    val fallbackLoadError = stringResource(stringRes.feed_list_load_error)

    Screen(
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.add_feed_feeds),
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                }
            )
        },
        onEffect = {
            when (it) {
                else -> viewModel.unhandledEffect(it)
            }
        }
    ) { state ->
        Column {
            FilterTabs(state.selectedTab) {
                viewModel.onEvent(FeedListScreenEvent.SelectTab(it))
            }

            when (refreshState) {
                is LoadState.Error -> FeedListError(
                    message = refreshState.error.message ?: fallbackLoadError,
                    onRetry = feeds::retry,
                )

                is LoadState.NotLoading if feeds.itemCount == 0 -> EmptyFeedList()
                else -> FeedsList(feeds, onTogglePinned = { feed, pinned ->
                    viewModel.onEvent(
                        FeedListScreenEvent.TogglePinned(
                            id = feed.id,
                            pinned = pinned,
                        )
                    )
                })
            }
        }
    }
}

@Composable
internal fun FeedsList(
    feeds: LazyPagingItems<UIFeed>,
    onTogglePinned: (UIFeed, Boolean) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive((WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND / 2).dp),
        contentPadding = PaddingValues(LocalDPSpacing.current.md),
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.lg),
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.lg)
    ) {
        items(
            count = feeds.itemCount,
            key = { index -> feeds[index]?.id ?: "feed-placeholder-$index" },
        ) { index ->
            val feed = feeds[index]
            if (feed == null) {
                FeedListPlaceholderRow()
            } else {
                FeedListRow(
                    feed = feed,
                    onTogglePinned = {
                        onTogglePinned(feed, it)
                    },
                )
            }
        }

        when (feeds.loadState.append) {
            is LoadState.Loading -> item(key = "append-loading") {
                AppendLoadingRow()
            }

            is LoadState.Error -> item(key = "append-error") {
                AppendErrorRow(onRetry = feeds::retry)
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
