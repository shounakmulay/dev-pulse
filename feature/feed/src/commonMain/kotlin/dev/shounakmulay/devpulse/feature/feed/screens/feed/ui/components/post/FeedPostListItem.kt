package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.list.post.FeedPostListItem
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPostSearchResult

@Composable
fun FeedPostListItem(
    post: UIFeedPostSearchResult,
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
        title = post.search?.highlightedTitle ?: TextResource.fromText(post.post.title),
        description = post.search?.highlightedDescription
            ?: post.post.summary?.let { TextResource.fromText(it) },
        context = post.search?.highlightedContent,
        imageUrl = post.post.imageUrl,
        websiteImageUrl = post.feed.websiteImageUrl,
        feedInitials = post.feed.initials,
        feedTitle = post.feed.title,
        publishedText = post.post.publishedText,
        createdAt = post.post.createdAt,
        bookmarked = post.post.bookmarked,
        onBookmarkChanged = { onBookmarkChanged(post.post, it) },
        onPostClick = { onPostClick(post.post) }
    )
}

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
        title = TextResource.fromText(post.title),
        description = post.summary?.let { TextResource.fromText(it) },
        context = null,
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