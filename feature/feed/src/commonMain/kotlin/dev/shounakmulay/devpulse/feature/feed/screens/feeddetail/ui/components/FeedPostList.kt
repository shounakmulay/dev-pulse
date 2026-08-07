package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterState
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postsListEmptyMessage
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedsPostListItemVariant

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
internal fun FeedPostList(
    lazyGridState: LazyGridState,
    uiFeed: UIFeed,
    feed: RssFeed,
    posts: LazyPagingItems<UIFeedPost>,
    postSortAndFilterState: PostSortAndFilterState,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
    onFilterUpdated: (RssPostFilter) -> Unit,
    onSortUpdated: (UIPostSort) -> Unit,
    clearFilters: () -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        state = lazyGridState,
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(vertical = LocalDPSpacing.current.md),
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box {
                DPImage(
                    modifier = Modifier
                        .matchParentSize()
                        .blur(
                            radius = 32.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                        .alpha(0.1f),
                    url = uiFeed.websiteImageUrl.orEmpty(),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = ""
                )
                FeedHeader(uiFeed, feed)
            }
        }

        stickyHeader {
            PostSortAndFilters(
                state = postSortAndFilterState,
                onFilterUpdated = onFilterUpdated,
                onSortUpdated = onSortUpdated,
                clearFilters = clearFilters
            )
        }

        if (posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0) {
            postsListEmptyMessage(clearFilters)
        }

        items(
            count = posts.itemCount,
            key = posts.itemKey { it.id.value }
        ) { index ->
            val post = posts[index]
            if (post != null) {
                FeedPostListItem(
                    modifier = Modifier.padding(horizontal = LocalDPSpacing.current.md)
                        .animateItem(),
                    post = post,
                    showImage = true,
                    onPostClick = onPostClick,
                    onBookmarkChanged = onBookmarkChanged,
                    variant = FeedsPostListItemVariant.M
                )
            }
        }
    }
}


