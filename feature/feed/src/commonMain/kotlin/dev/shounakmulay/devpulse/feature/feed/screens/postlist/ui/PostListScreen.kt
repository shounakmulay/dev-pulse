package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.components.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedsPostListItemVariant
import devpulse.core.resources.generated.resources.all_posts
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostListScreen(
    navigator: Navigator,
    viewModel: PostListViewModel = koinViewModel(),
) {
    val lazyGridState = rememberLazyGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    Screen(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.all_posts),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                },
            )
        },
        floatingActionButton = {
            ScrollToTopFAB(lazyGridState, scrollBehavior)
        },
        onEffect = { },
    ) { state ->
        Column {
            val posts = viewModel.posts.collectAsLazyPagingItems()

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                state = lazyGridState,
                columns = GridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(LocalDPSpacing.current.lg),
                horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
                verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
            ) {
                stickyHeader {
                    PostSortAndFilters(
                        sortValues = state.postFilterSortState.sortValues,
                        onSortUpdated = {
                            viewModel.onEvent(PostListScreenEvent.OnSortUpdated(it))
                            lazyGridState.requestScrollToItem(0)
                        },
                        filters = state.postFilterSortState.filters,
                        onFilterUpdated = {
                            viewModel.onEvent(PostListScreenEvent.OnFilterUpdated(it))
                            lazyGridState.requestScrollToItem(0)
                        },
                        clearFilters = {
                            viewModel.onEvent(PostListScreenEvent.ClearFilters)
                            lazyGridState.requestScrollToItem(0)
                        }
                    )
                }
                items(posts.itemCount, key = posts.itemKey { it.id }) { index ->
                    val post = posts[index]
                    if (post != null) {
                        FeedPostListItem(
                            modifier = Modifier.animateItem(),
                            post = post,
                            variant = FeedsPostListItemVariant.L,
                            onBookmarkChanged = { selectedPost, bookmarked ->
                                viewModel.onEvent(
                                    PostListScreenEvent.OnPostBookmarkChanged(
                                        postId = selectedPost.id,
                                        bookmarked = bookmarked
                                    )
                                )
                            },
                            onPostClick = {

                            }
                        )
                    }
                }
            }
        }
    }
}
