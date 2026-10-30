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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterState
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.postsListEmptyMessage
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
internal fun FeedPostList(
    lazyGridState: LazyGridState,
    uiFeed: UIFeed,
    feed: RssFeed,
    posts: LazyPagingItems<UIFeedPost>,
    onReadChanged: (UUID, Boolean) -> Unit = { _, _ -> },
    postSortAndFilterState: PostSortAndFilterState,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
    onOpenInBrowser: (String) -> Unit,
    onFilterUpdated: (RssPostFilter) -> Unit,
    onSortUpdated: (UIPostSort) -> Unit,
    clearFilters: () -> Unit,
    postListItemVariant: FeedsPostListItemVariant,
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
                            edgeTreatment = BlurredEdgeTreatment.Unbounded,
                        )
                        .alpha(0.1f),
                    url = uiFeed.websiteImageUrl.orEmpty(),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "",
                )
                FeedHeader(uiFeed, feed)
            }
        }

        stickyHeader {
            PostSortAndFilters(
                state = postSortAndFilterState,
                onFilterUpdated = onFilterUpdated,
                onSortUpdated = onSortUpdated,
                clearFilters = clearFilters,
            )
        }

        if (posts.loadState.refresh is LoadState.NotLoading && posts.itemCount == 0) {
            postsListEmptyMessage(
                readStatus = postSortAndFilterState.filters.filterIsInstance<RssPostFilter.ReadStatus>()
                    .firstOrNull()?.value,
                showAllPosts = { onFilterUpdated(RssPostFilter.ReadStatus(null)) },
                clearFilters = clearFilters,
            )
        }

        items(
            count = posts.itemCount,
            key = posts.itemKey { it.id.value },
        ) { index ->
            val post = posts[index]
            if (post != null) {
                FeedPostListItem(
                    modifier = Modifier.padding(horizontal = LocalDPSpacing.current.sm)
                        .animateItem(),
                    post = post,
                    onReadChanged = onReadChanged,
                    showImage = true,
                    onPostClick = onPostClick,
                    onOpenInBrowser = onOpenInBrowser,
                    onBookmarkChanged = onBookmarkChanged,
                    variant = postListItemVariant,
                )
            }
        }
    }
}

@DPScreenPreview
@Composable
private fun FeedPostListPreview() {
    Preview {
        val posts = remember {
            MutableStateFlow(PagingData.from(listOf(FeedPreviewData.post)))
        }.collectAsLazyPagingItems()
        FeedPostList(
            lazyGridState = rememberLazyGridState(),
            uiFeed = FeedPreviewData.feed,
            feed = FeedPreviewData.rssFeed,
            posts = posts,
            postSortAndFilterState = PostSortAndFilterState(
                screen = Screen.Tabs.Feed,
                filters = persistentListOf(),
                sortValues = persistentListOf(),
            ),
            onBookmarkChanged = { _, _ -> },
            onPostClick = {},
            onOpenInBrowser = {},
            onFilterUpdated = {},
            onSortUpdated = {},
            clearFilters = {},
            postListItemVariant = FeedsPostListItemVariant.DEFAULT,
        )
    }
}
