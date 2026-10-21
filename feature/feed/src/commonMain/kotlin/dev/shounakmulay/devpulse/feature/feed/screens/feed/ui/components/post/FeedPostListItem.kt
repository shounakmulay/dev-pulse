package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.list.post.FeedPostListItem
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData

@Composable
fun FeedPostListItem(
    post: UIFeedPost,
    variant: FeedsPostListItemVariant,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
) {
    FeedPostListItem(
        modifier = modifier,
        variant = variant,
        showImage = showImage,
        title = post.search?.highlightedTitle ?: TextResource.fromText(post.title),
        description = post.search?.highlightedDescription
            ?: post.summary?.let { TextResource.fromText(it) },
        context = post.search?.highlightedContent,
        imageUrl = post.imageUrl,
        websiteImageUrl = post.feed.websiteImageUrl,
        feedInitials = post.feed.initials,
        feedTitle = post.feed.title,
        publishedText = post.publishedText,
        createdAt = post.createdAt,
        bookmarked = post.bookmarked,
        onBookmarkChanged = { onBookmarkChanged(post, it) },
        onPostClick = { onPostClick(post) },
    )
}

@DPScreenPreview
@Composable
private fun FeedPostListItemPreview() {
    Preview {
        LazyColumn {
            items(FeedsPostListItemVariant.entries) { variant ->
                FeedPostListItem(
                    post = FeedPreviewData.post,
                    variant = variant,
                    showImage = false,
                    onBookmarkChanged = { _, _ -> },
                    onPostClick = {},
                )
            }
        }
    }
}
