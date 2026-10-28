package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveColumnsCount
import dev.shounakmulay.devpulse.core.ui.text.isNotNullOrEmpty
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterEvent
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterViewModel
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postsListEmptyMessage
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.PostListScreenState
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun PostsList(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    postSortAndFilterViewModel: PostSortAndFilterViewModel,
    posts: LazyPagingItems<UIFeedPost>,
    onReadChanged: (UUID, Boolean) -> Unit = { _, _ -> },
    onClearFilters: () -> Unit,
    state: PostListScreenState,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UUID) -> Unit,
) {
    val sortAndFilterState by postSortAndFilterViewModel.collectAsState()
    val readStatus = sortAndFilterState.filters.filterIsInstance<RssPostFilter.ReadStatus>().firstOrNull()?.value
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
                Column {
                    if (posts.loadState.refresh is LoadState.Loading) {
                        DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else {
                        Spacer(
                            Modifier.fillMaxWidth().height(4.dp)
                                .background(MaterialTheme.colorScheme.background),
                        )
                    }
                    PostSortAndFilters(
                        viewModel = postSortAndFilterViewModel,
                        onSortAndFilterDataChanged = {
                            lazyGridState.requestScrollToItem(0)
                        },
                    )
                }
            }

            if (posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0) {
                postsListEmptyMessage(
                    readStatus = readStatus,
                    showAllPosts = {
                        postSortAndFilterViewModel.onEvent(
                            PostSortAndFilterEvent.OnFilterUpdated(RssPostFilter.ReadStatus(null)),
                        )
                    },
                    clearFilters = onClearFilters,
                )
            }

            items(count = posts.itemCount, key = posts.itemKey { it.id.value }) { index ->
                val post = posts[index]
                if (post != null) {
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
                        onPostClick = { onPostClick(it.id) },
                    )
                }
            }
        }
    }
}
