package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost

@Composable
fun FeedPostGridCard(
    article: UIFeedPost,
    modifier: Modifier = Modifier,
    showImage: Boolean,
    onPostClick: (UIFeedPost) -> Unit,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
) {
    FeedPostListItem(
        post = article,
        showImage = showImage,
        variant = FeedsPostListItemVariant.M,
        onBookmarkChanged = onBookmarkChanged,
        onPostClick = onPostClick,
        modifier = modifier
            .width(if (showImage) 340.dp else 280.dp)
    )
}
