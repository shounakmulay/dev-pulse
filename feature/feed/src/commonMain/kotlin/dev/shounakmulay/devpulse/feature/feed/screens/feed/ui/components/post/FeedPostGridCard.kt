package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost

@Composable
fun FeedPostGridCard(
    article: UIFeedPost,
    modifier: Modifier = Modifier,
    showImage: Boolean,
    onPostClick: (UIFeedPost) -> Unit,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
) {

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(if (showImage) 340.dp else 280.dp)
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = { onPostClick(article) },
                onLongClick = {},
                onDoubleClick = {
                    onBookmarkChanged(article, !article.bookmarked)
                }
            ),
        border = BorderStroke(
            1.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.25f)
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        FeedPostListItem(
            post = article,
            showImage = showImage,
            variant = FeedsPostListItemVariant.M,
            onBookmarkChanged = onBookmarkChanged,
            modifier = Modifier.padding(
                DPTheme.spacing.md
            )
        )
    }
}
