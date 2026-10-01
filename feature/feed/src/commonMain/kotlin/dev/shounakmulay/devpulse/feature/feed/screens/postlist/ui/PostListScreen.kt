package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.screen.SearchScreen
import dev.shounakmulay.devpulse.core.ui.text.isNotNullOrEmpty
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterEvent
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterViewModel
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postsListEmptyMessage
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    screen: Screen.Tabs.Feed.PostList,
    navigator: Navigator,
    postSortAndFilterViewModel: PostSortAndFilterViewModel = koinViewModel { parametersOf(screen) },
    viewModel: PostListViewModel = koinViewModel {
        parametersOf(postSortAndFilterViewModel.sortAndFiltersFlow)
    },
) {
    val lazyGridState = rememberLazyGridState()
    val searchResults by viewModel.postSearchResults.collectAsState(initial = emptyList())
    val searchBarState = rememberContainedSearchBarState()
    val coroutineScope = rememberCoroutineScope()

    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    SearchScreen(
        viewModel = viewModel,
        searchBarState = searchBarState,
        initialQuery = viewModel.state.value.searchQuery,
        onQueryChange = { viewModel.onEvent(PostListScreenEvent.OnSearchQueryChanged(it)) },
        onNavigateBack = navigator::navigateBack,
        floatingActionButton = { collapsedFraction ->
            ScrollToTopFAB(lazyGridState = lazyGridState, collapsedFraction = collapsedFraction)
        },
        onEffect = {},
        searchContent = { state ->
            if (state.searchLoading) {
                DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn {

                items(searchResults, key = { it.post.id.value }) {
                    FeedPostListItem(
                        modifier = Modifier.padding(
                            horizontal = LocalDPSpacing.current.sm,
                            vertical = LocalDPSpacing.current.xs
                        ),
                        showImage = false,
                        post = it,
                        variant = when {
                            it.search?.highlightedContent.isNotNullOrEmpty() -> FeedsPostListItemVariant.M
                            it.search?.highlightedDescription.isNotNullOrEmpty() -> FeedsPostListItemVariant.S
                            else -> FeedsPostListItemVariant.XS
                        },
                        onBookmarkChanged = { post, bookmarked ->
                            viewModel.onEvent(
                                PostListScreenEvent.OnPostBookmarkChanged(
                                    postId = post.id,
                                    bookmarked = bookmarked
                                )
                            )
                        },
                        onPostClick = {
                            navigator.navigate(
                                Screen.Tabs.Feed.PostDetail(it.id),
                                onRootStack = true
                            )
                            coroutineScope.launch {
                                searchBarState.animateToCollapsed()
                            }
                        }
                    )
                }
            }
        },
    ) {
        Column {
            PostSortAndFilters(
                viewModel = postSortAndFilterViewModel,
                onSortAndFilterDataChanged = {
                    lazyGridState.requestScrollToItem(0)
                }
            )
            val posts = viewModel.posts.collectAsLazyPagingItems()

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                state = lazyGridState,
                columns = GridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(LocalDPSpacing.current.lg),
                horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
                verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
            ) {
                if (posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0) {
                    postsListEmptyMessage {
                        postSortAndFilterViewModel.onEvent(PostSortAndFilterEvent.OnClearFilters)
                    }
                }

                items(posts.itemCount, key = posts.itemKey { it.id.value }) { index ->
                    val post = posts[index]
                    if (post != null) {
                        FeedPostListItem(
                            modifier = Modifier.animateItem(),
                            post = post,
                            variant = it.feedPostListItemVariant,
                            onBookmarkChanged = { selectedPost, bookmarked ->
                                viewModel.onEvent(
                                    PostListScreenEvent.OnPostBookmarkChanged(
                                        postId = selectedPost.id,
                                        bookmarked = bookmarked
                                    )
                                )
                            },
                            onPostClick = {
                                navigator.navigate(
                                    Screen.Tabs.Feed.PostDetail(it.id),
                                    onRootStack = true
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
