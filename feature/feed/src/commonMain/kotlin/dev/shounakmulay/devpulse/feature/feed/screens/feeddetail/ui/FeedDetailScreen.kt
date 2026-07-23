package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPFAB
import dev.shounakmulay.devpulse.core.designsystem.components.DPFABStyle
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed.FeedDetail
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components.FeedDetailTopAppBar
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components.FeedPostList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

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
            val visible by remember {
                derivedStateOf { lazyGridState.firstVisibleItemIndex > 0 && scrollBehavior.state.collapsedFraction in 0f..0.5f }
            }
            val coroutineScope = rememberCoroutineScope()
            DPFAB(
                icon = DPIcons.ChevronUp,
                visible = visible,
                style = DPFABStyle.Secondary,
                onClick = {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
            )
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
            filters = state.filters,
            sortValues = state.sort.toImmutableList(),
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
            },
            onSortUpdated = {
                viewModel.onEvent(FeedDetailScreenEvent.OnSortUpdated(it))
            }
        )
    }
}
