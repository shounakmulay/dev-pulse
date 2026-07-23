package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.feature.feed.components.PostSortAndFilters
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostGridCard
import kotlinx.collections.immutable.ImmutableList

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
internal fun FeedPostList(
    lazyGridState: LazyGridState,
    uiFeed: UIFeed,
    feed: RssFeed,
    posts: LazyPagingItems<UIFeedPost>,
    filters: ImmutableList<RssPostFilter>,
    sortValues: ImmutableList<UIPostSort>,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
    onFilterUpdated: (RssPostFilter) -> Unit,
    onSortUpdated: (UIPostSort) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        state = lazyGridState,
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(LocalDPSpacing.current.md),
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
                sortValues = sortValues,
                onSortUpdated = onSortUpdated,
                filters = filters,
                onFilterUpdated = onFilterUpdated
            )
        }

        items(
            count = posts.itemCount,
            key = posts.itemKey { it.id }
        ) { index ->
            val post = posts[index]
            if (post != null) {
                FeedPostGridCard(
                    modifier = Modifier.animateItem(),
                    article = post,
                    showImage = true,
                    onPostClick = onPostClick,
                    onBookmarkChanged = onBookmarkChanged
                )
            }
        }
    }
}
