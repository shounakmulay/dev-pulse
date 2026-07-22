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
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostGridCard
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun FeedPostList(
    lazyGridState: LazyGridState,
    uiFeed: UIFeed,
    feed: RssFeed,
    posts: LazyPagingItems<UIFeedPost>,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
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

        items(
            count = posts.itemCount,
            key = posts.itemKey { it.id }
        ) { index ->
            val post = posts[index]
            if (post != null) {
                FeedPostGridCard(
                    article = post,
                    showImage = true,
                    onPostClick = onPostClick,
                    onBookmarkChanged = onBookmarkChanged
                )
            }
        }
    }
}
