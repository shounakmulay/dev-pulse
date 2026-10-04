package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.list.post.FeedPostListItem
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost

@Composable
fun FeedPostListItem(
    post: UIFeedPost,
    variant: FeedsPostListItemVariant,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit
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
        onPostClick = { onPostClick(post) }
    )
}
