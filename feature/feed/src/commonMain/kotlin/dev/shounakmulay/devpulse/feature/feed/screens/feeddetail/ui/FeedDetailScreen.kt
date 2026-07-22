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
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed.FeedDetail
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

    Screen(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            FeedDetailTopAppBar(
                scrollBehavior, lazyGridState, navigator,
                onPinToggled = {
                    viewModel.onEvent(
                        FeedDetailScreenEvent.PinToggled
                    )
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
            posts = posts,
            onBookmarkChanged = { selectedPost, bookmarked ->
                viewModel.onEvent(
                    FeedDetailScreenEvent.OnPostBookmarkChanged(
                        postId = selectedPost.id,
                        bookmarked = bookmarked
                    )
                )
            }, onPostClick = {
            }
        )
    }
}
