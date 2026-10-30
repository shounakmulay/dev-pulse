package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.list.post.FeedPostListItem
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.components.postRead.PostReadMenu
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData

@Composable
fun FeedPostListItem(
    post: UIFeedPost,
    variant: FeedsPostListItemVariant,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    onReadChanged: (UUID, Boolean) -> Unit = { _, _ -> },
    onBookmarkChanged: (UIFeedPost, Boolean) -> Unit,
    onPostClick: (UIFeedPost) -> Unit,
) {
    var menuExpanded by remember(post.id) { mutableStateOf(false) }
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
        isRead = post.isRead,
        actions = {
            PostReadMenu(
                post = post,
                expanded = menuExpanded,
                onExpandedChange = { menuExpanded = it },
                onReadChanged = onReadChanged,
            )
        },
        onBookmarkChanged = { onBookmarkChanged(post, it) },
        onPostClick = { onPostClick(post) },
        onPostLongClick = { menuExpanded = true },
    )
}

@DPScreenPreview
@Composable
private fun FeedPostListItemPreview() {
    Preview {
        LazyColumn {
            items(
                FeedsPostListItemVariant.entries.flatMap { variant ->
                    listOf(variant to false, variant to true)
                },
            ) { (variant, isRead) ->
                FeedPostListItem(
                    post = FeedPreviewData.post.copy(isRead = isRead),
                    variant = variant,
                    showImage = false,
                    onBookmarkChanged = { _, _ -> },
                    onPostClick = {},
                )
            }
        }
    }
}
