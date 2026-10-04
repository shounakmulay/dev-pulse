package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveColumnsCount
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendErrorRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendLoadingRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.EmptyFeedList
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListError
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListPlaceholderRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FilterTabs
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import devpulse.core.resources.generated.resources.feed_list_load_error
import devpulse.core.resources.generated.resources.feed_search
import org.jetbrains.compose.resources.stringResource
import dev.shounakmulay.devpulse.core.ui.screen.Screen as MviScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedListScreen(
    viewModel: FeedListViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyGridState = rememberLazyGridState()
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val feeds = viewModel.uiFeedsFlow.collectAsLazyPagingItems()

    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    LaunchedEffect(lazyGridState, keyboardController) {
        snapshotFlow { lazyGridState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolling) {
                keyboardController?.hide()
            }
        }
    }

    MviScreen(
        modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            DPSearchTopAppBar(
                scrollBehavior = appBarScrollBehavior,
                navigationIcon = {
                    DPBackNavigationIconButton(modifier = it, onNavigateBack = navigator::navigateBack)
                },
                textValue = searchQuery,
                onTextValueChange = {
                    viewModel.onEvent(FeedListScreenEvent.OnSearchQueryChanged(it))
                },
                placeholder = stringResource(stringRes.feed_search),
            )
        },
        floatingActionButton = {
            ScrollToTopFAB(lazyGridState = lazyGridState, collapsedFraction = 0f) {
                appBarScrollBehavior.state.heightOffset = 0f
            }
        },
        onEffect = { viewModel.unhandledEffect(it) },
    ) { state ->
        FeedsList(
            feeds = feeds,
            lazyGridState = lazyGridState,
            selectedTab = state.selectedTab,
            onTabSelected = { tab ->
                if (tab != state.selectedTab) {
                    lazyGridState.requestScrollToItem(0)
                    viewModel.onEvent(FeedListScreenEvent.SelectTab(tab))
                }
            },
            onTogglePinned = { feed, pinned ->
                viewModel.onEvent(FeedListScreenEvent.TogglePinned(id = feed.id, pinned = pinned))
            },
            onFeedItemClick = {
                navigator.replaceOfSameType(Screen.Tabs.Feed.FeedDetail(it.id))
            },
        )
    }
}

@Composable
internal fun FeedsList(
    feeds: LazyPagingItems<UIFeed>,
    lazyGridState: LazyGridState,
    selectedTab: UISelectedTab,
    onTabSelected: (UISelectedTab) -> Unit,
    onTogglePinned: (UIFeed, Boolean) -> Unit,
    onFeedItemClick: (UIFeed) -> Unit
) {
    val fallbackLoadError = stringResource(stringRes.feed_list_load_error)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState,
        columns = adaptiveColumnsCount(),
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
            is LoadState.Error -> item(key = "refresh-error", span = { GridItemSpan(maxLineSpan) }) {
                FeedListError(
                    message = refreshState.error.message ?: fallbackLoadError,
                    onRetry = feeds::retry,
                )
            }

            is LoadState.NotLoading if feeds.itemCount == 0 ->
                item(key = "empty-feeds", span = { GridItemSpan(maxLineSpan) }) {
                    EmptyFeedList()
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
                FeedListRow(
                    feed = feed,
                    onTogglePinned = {
                        onTogglePinned(feed, it)
                    },
                    onClick = {
                        onFeedItemClick(feed)
                    }
                )
            }
        }

        when (feeds.loadState.append) {
            is LoadState.Loading -> item(key = "append-loading", span = { GridItemSpan(maxLineSpan) }) {
                AppendLoadingRow()
            }

            is LoadState.Error -> item(key = "append-error", span = { GridItemSpan(maxLineSpan) }) {
                AppendErrorRow(onRetry = feeds::retry)
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
