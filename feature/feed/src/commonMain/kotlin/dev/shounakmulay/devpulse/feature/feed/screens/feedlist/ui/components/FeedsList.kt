package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveColumnsCount
import dev.shounakmulay.devpulse.feature.feed.components.feed.EmptyFeedsImportCTA
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenu
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsTarget
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import devpulse.core.resources.generated.resources.feed_list_load_error
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeedsList(
    feeds: LazyPagingItems<UIFeed>,
    lazyGridState: LazyGridState,
    selectedTab: UISelectedTab,
    onTabSelected: (UISelectedTab) -> Unit,
    onTogglePinned: (UIFeed, Boolean) -> Unit,
    selectedOptions: FeedOptionsTarget?,
    onShowOptions: (UIFeed) -> Unit,
    onDismissOptions: (UUID) -> Unit,
    onOptionSelected: (UUID, FeedOptionsMenuItem) -> Unit,
    onFeedItemClick: (UIFeed) -> Unit,
    onNavigationToImportFeeds: () -> Unit
) {
    val fallbackLoadError = stringResource(stringRes.feed_list_load_error)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState,
        columns = GridCells.Fixed(adaptiveColumnsCount()),
        contentPadding = PaddingValues(bottom = LocalDPSpacing.current.listItemHeight),
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
    ) {
        stickyHeader(key = "feed-filters") {
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                if (feeds.loadState.refresh is LoadState.Loading) {
                    DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Spacer(Modifier.fillMaxWidth().height(4.dp))
                }
                FilterTabs(selectedTab = selectedTab, onClick = onTabSelected)
            }
        }

        when (val refreshState = feeds.loadState.refresh) {
            is LoadState.Error -> item(
                key = "refresh-error",
                span = { GridItemSpan(maxLineSpan) }) {
                FeedListError(
                    message = refreshState.error.message ?: fallbackLoadError,
                    onRetry = feeds::retry,
                )
            }

            is LoadState.NotLoading if feeds.itemCount == 0 ->
                item(key = "empty-feeds", span = { GridItemSpan(maxLineSpan) }) {
                    EmptyFeedsImportCTA(
                        modifier = Modifier.padding(top = LocalDPSpacing.current.xxxl),
                        onNavigateToAddFeed = onNavigationToImportFeeds
                    )
                }

            else -> Unit
        }

        items(
            count = feeds.itemCount,
            key = { index -> feeds.peek(index)?.id?.value ?: "feed-placeholder-$index" },
        ) { index ->
            val feed = feeds[index]
            if (feed == null) {
                FeedListPlaceholderRow()
            } else {
                var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                pressOffset =
                                    DpOffset(down.position.x.toDp(), down.position.y.toDp())
                            }
                        }
                ) {
                    FeedListRow(
                        feed = feed,
                        onTogglePinned = { onTogglePinned(feed, it) },
                        onLongClick = { onShowOptions(feed) },
                        onClick = { onFeedItemClick(feed) },
                    )
                    if (selectedOptions?.feedId == feed.id) {
                        DisposableEffect(feed.id) {
                            onDispose { onDismissOptions(feed.id) }
                        }
                        Box(Modifier.offset(pressOffset.x, pressOffset.y)) {
                            FeedOptionsMenu(
                                expanded = true,
                                menuItems = selectedOptions.items,
                                onMenuItemSelected = { onOptionSelected(feed.id, it) },
                                onDismissRequest = { onDismissOptions(feed.id) },
                            )
                        }
                    }
                }
            }
        }

        when (feeds.loadState.append) {
            is LoadState.Loading -> item(
                key = "append-loading",
                span = { GridItemSpan(maxLineSpan) }) {
                AppendLoadingRow()
            }

            is LoadState.Error -> item(key = "append-error", span = { GridItemSpan(maxLineSpan) }) {
                AppendErrorRow(onRetry = feeds::retry)
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
