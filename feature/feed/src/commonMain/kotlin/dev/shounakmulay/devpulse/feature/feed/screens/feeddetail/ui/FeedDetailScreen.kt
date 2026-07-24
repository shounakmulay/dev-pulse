package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed.FeedDetail
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components.FeedDetailTopAppBar
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components.FeedPostList

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedDetailScreen(
    route: FeedDetail,
    navigator: Navigator,
    viewModel: FeedDetailViewModel,
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyGridState = rememberLazyGridState()

    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    Screen(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            FeedDetailTopAppBar(
                scrollBehavior = scrollBehavior,
                lazyGridState = lazyGridState,
                navigator = navigator,
                onPinToggled = {
                    viewModel.onEvent(
                        FeedDetailScreenEvent.PinToggled
                    )
                }
            )
        },
        floatingActionButton = {
            ScrollToTopFAB(lazyGridState, scrollBehavior)
        },
        onEffect = {},
    ) { state ->
        if (state.isLoading || state.feed == null || state.uiFeed == null)
            return@Screen LoadingIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

        FeedPostList(
            lazyGridState = lazyGridState,
            uiFeed = state.uiFeed,
            feed = state.feed,
            filters = state.postFilterSortState.filters,
            sortValues = state.postFilterSortState.sortValues,
            posts = posts,
            onBookmarkChanged = { selectedPost, bookmarked ->
                viewModel.onEvent(
                    FeedDetailScreenEvent.OnPostBookmarkChanged(
                        postId = selectedPost.id,
                        bookmarked = bookmarked
                    )
                )
            },
            onPostClick = {
            },
            onFilterUpdated = {
                viewModel.onEvent(FeedDetailScreenEvent.OnFilterUpdated(it))
                lazyGridState.requestScrollToItem(0)
            },
            onSortUpdated = {
                viewModel.onEvent(FeedDetailScreenEvent.OnSortUpdated(it))
                lazyGridState.requestScrollToItem(0)
            },
            clearFilters = {
                viewModel.onEvent(FeedDetailScreenEvent.ClearFilters)
                lazyGridState.requestScrollToItem(0)
            }
        )
    }
}


