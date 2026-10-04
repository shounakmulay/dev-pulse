package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveColumnsCount
import dev.shounakmulay.devpulse.core.ui.text.isNotNullOrEmpty
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterViewModel
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postsListEmptyMessage
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.PostListScreenState

@Composable
internal fun PostsList(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    postSortAndFilterViewModel: PostSortAndFilterViewModel,
    posts: LazyPagingItems<UIFeedPost>,
    onClearFilters: () -> Unit,
    state: PostListScreenState,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UUID) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        state = lazyGridState,
        columns = adaptiveColumnsCount(),
        contentPadding = PaddingValues(bottom = LocalDPSpacing.current.listItemHeight),
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
    ) {
        if (posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0) {
            postsListEmptyMessage {
                onClearFilters()
            }
        }

        stickyHeader {
            Column {
                if (posts.loadState.refresh is LoadState.Loading) {
                    DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Spacer(Modifier.fillMaxWidth().height(4.dp).background(MaterialTheme.colorScheme.background))
                }
                PostSortAndFilters(
                    viewModel = postSortAndFilterViewModel,
                    onSortAndFilterDataChanged = {
                        lazyGridState.requestScrollToItem(0)
                    },
                )

            }
        }

        items(count = posts.itemCount, key = posts.itemKey { it.id.value }) { index ->
            val post = posts[index]
            if (post != null) {
                FeedPostListItem(
                    modifier = Modifier
                        .padding(horizontal = LocalDPSpacing.current.lg)
                        .animateItem(),
                    post = post,
                    showImage = post.search == null,
                    variant = when {
                        post.search == null -> state.feedPostListItemVariant
                        post.search.highlightedContent.isNotNullOrEmpty() -> FeedsPostListItemVariant.M
                        post.search.highlightedDescription.isNotNullOrEmpty() -> FeedsPostListItemVariant.S
                        else -> FeedsPostListItemVariant.XS
                    },
                    onBookmarkChanged = onBookmarkChanged,
                    onPostClick = { onPostClick(it.id) }
                )
            }
        }
    }
}
