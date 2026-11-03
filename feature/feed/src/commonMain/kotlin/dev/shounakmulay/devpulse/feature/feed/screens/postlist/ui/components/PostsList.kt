package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveColumnsCount
import dev.shounakmulay.devpulse.core.ui.text.isNotNullOrEmpty
import dev.shounakmulay.devpulse.feature.feed.components.paging.showEmptyEntry
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostListEntry
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterEvent
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterViewModel
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostsListEmptyMessage
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postAppendFeedback
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postRefreshFeedback
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postSearchHint
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.PostListScreenState

@Composable
internal fun PostsList(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    postSortAndFilterViewModel: PostSortAndFilterViewModel,
    posts: LazyPagingItems<PostListEntry>,
    onReadChanged: (UUID, Boolean) -> Unit = { _, _ -> },
    onClearFilters: () -> Unit,
    onClearSearch: () -> Unit,
    onAddFeed: () -> Unit,
    state: PostListScreenState,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
    onOpenInBrowser: (String) -> Unit,
) {
    BoxWithConstraints {
        val columns = adaptiveColumnsCount()
        LazyVerticalGrid(
            modifier = modifier.fillMaxWidth(),
            state = lazyGridState,
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(bottom = LocalDPSpacing.current.listItemHeight),
            horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
            verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
        ) {
            stickyHeader {
                PostSortAndFilters(
                    viewModel = postSortAndFilterViewModel,
                    onSortAndFilterDataChanged = {
                        lazyGridState.requestScrollToItem(0)
                    },
                )
            }

            postSearchHint(state.searchQuery)
            postRefreshFeedback(posts)
            items(
                count = posts.itemCount,
                key = posts.itemKey { it.key },
                span = { index -> GridItemSpan(if (posts.peek(index) is PostListEntry.Empty) maxLineSpan else 1) },
            ) { index ->
                when (val entry = posts[index]) {
                    is PostListEntry.Empty -> if (posts.showEmptyEntry) {
                        PostsListEmptyMessage(
                            entry = entry,
                            onClearSearch = onClearSearch,
                            onShowAllPosts = { postSortAndFilterViewModel.onEvent(PostSortAndFilterEvent.OnFilterUpdated(RssPostFilter.ReadStatus(null))) },
                            onClearFilters = onClearFilters,
                            onAddFeed = onAddFeed,
                        )
                    }

                    is PostListEntry.Post -> {
                        val post = entry.post
                        FeedPostListItem(
                            modifier = Modifier
                                .padding(horizontal = LocalDPSpacing.current.sm)
                                .animateItem(),
                            post = post,
                            onReadChanged = onReadChanged,
                            showImage = post.search == null,
                            variant = when {
                                post.search == null -> state.feedPostListItemVariant
                                post.search.highlightedContent.isNotNullOrEmpty() -> FeedsPostListItemVariant.M
                                post.search.highlightedDescription.isNotNullOrEmpty() -> FeedsPostListItemVariant.S
                                else -> FeedsPostListItemVariant.XS
                            },
                            onBookmarkChanged = onBookmarkChanged,
                            onPostClick = onPostClick,
                            onOpenInBrowser = onOpenInBrowser,
                        )
                    }

                    null -> Unit
                }
            }
            postAppendFeedback(posts)
        }
    }
}
