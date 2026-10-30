package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData

@Composable
fun FeedPostGridCard(
    article: UIFeedPost,
    modifier: Modifier = Modifier,
    showImage: Boolean,
    onReadChanged: (UUID, Boolean) -> Unit = { _, _ -> },
    onPostClick: (UIFeedPost) -> Unit,
    onOpenInBrowser: (String) -> Unit,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
) {
    FeedPostListItem(
        post = article,
        onReadChanged = onReadChanged,
        showImage = showImage,
        variant = FeedsPostListItemVariant.M,
        onBookmarkChanged = onBookmarkChanged,
        onPostClick = onPostClick,
        onOpenInBrowser = onOpenInBrowser,
        modifier = modifier
            .width(if (showImage) 340.dp else 280.dp),
    )
}

@DPComponentPreview
@Composable
private fun FeedPostGridCardPreview() {
    Preview {
        FeedPostGridCard(
            article = FeedPreviewData.post,
            showImage = false,
            onPostClick = {},
            onOpenInBrowser = {},
            onBookmarkChanged = { _, _ -> },
        )
    }
}
