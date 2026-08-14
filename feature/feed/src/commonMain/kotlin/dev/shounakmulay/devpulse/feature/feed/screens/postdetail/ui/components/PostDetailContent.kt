package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextDot
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.core.ui.sharing.rememberSharingService
import dev.shounakmulay.devpulse.core.ui.transition.sharedBounds
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown.DPMarkdown
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.PostDetailScreenState
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection

@Composable
internal fun PostDetailContent(
    state: PostDetailScreenState,
    listState: LazyListState,
    onBookmarkChanged: (Boolean) -> Unit,
    onOpenInWebView: (String) -> Unit,
    onOpenContentTextSettings: () -> Unit,
    onImageClick: (link: String) -> Unit
) {
    DPMarkdown(
        markdown = when (state.selectedSection) {
            PostDetailScreenSection.RSS -> state.rssContent?.content
            PostDetailScreenSection.EXTRACTED -> state.content?.content
        }.orEmpty(),
        listState = listState,
        header = {
            postDetailContentHeader(state.post, onImageClick)
            postDetailContentHeaderActions(
                post = state.post,
                onBookmarkChanged = onBookmarkChanged,
                onOpenInWebView = onOpenInWebView,
                onOpenContentTextSettings = onOpenContentTextSettings
            )
        },
        footer = { },
        onImageClick = onImageClick
    )
}

private fun LazyListScope.postDetailContentHeaderActions(
    post: UIFeedPost?,
    onBookmarkChanged: (Boolean) -> Unit,
    onOpenInWebView: (String) -> Unit,
    onOpenContentTextSettings: () -> Unit,
) {
    if (post == null) return
    stickyHeader {
        DPSectionDivider(shape = RectangleShape) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(
                    vertical = LocalDPSpacing.current.xs,
                    horizontal = LocalDPSpacing.current.xxs
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    Modifier.padding(
                        end = LocalDPSpacing.current.sm
                    )
                ) {
                    DPIconButton(
                        icon = DPIcons.FormatText,
                        variant = DPIconButtonVariant.Tertiary,
                        contentDescription = "",
                        size = DPSize.Small,
                        onClick = onOpenContentTextSettings
                    )
                }
                Row(
                    Modifier
                        .weight(1f, fill = false)
                        .padding(
                            start = LocalDPSpacing.current.sm,
                        )
                ) {
                    DPIconButton(
                        icon = DPIcons.OpenInBrowser,
                        variant = DPIconButtonVariant.Tertiary,
                        contentDescription = "",
                        size = DPSize.Small,
                    ) {
                        post.articleUrl?.let { url -> onOpenInWebView(url) }
                    }
                    val sharingService = rememberSharingService {
                        //TODO : Show toast
                    }
                    DPIconButton(
                        icon = DPIcons.Share,
                        variant = DPIconButtonVariant.Tertiary,
                        contentDescription = "",
                        size = DPSize.Small
                    ) {
                        sharingService.share(
                            text = post.title,
                            link = post.articleUrl ?: post.sourceUrl ?: post.sourceName
                        )
                    }
                    DPIconButton(
                        icon = if (post.bookmarked) DPIcons.BookmarkAdded else DPIcons.BookmarkAddOutline,
                        contentDescription = "",
                        size = DPSize.Small
                    ) {
                        onBookmarkChanged(!post.bookmarked)
                    }
                }
            }
        }
    }
}

private fun LazyListScope.postDetailContentHeader(
    post: UIFeedPost?,
    onImageClick: (String) -> Unit
) {
    if (post == null) return
    item("ArticleHeader") {
        Column(Modifier.fillMaxWidth().padding(horizontal = LocalDPSpacing.current.md)) {
            if (!post.imageUrl.isNullOrBlank()) {
                DPImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = LocalDPSpacing.current.lg)
                        .clickable {
                            onImageClick(post.imageUrl)
                        }
                        .clip(MaterialTheme.shapes.large)
                        .sharedBounds(
                            key = "Image${post.imageUrl}",
                            clipShape = MaterialTheme.shapes.large
                        ),
                    url = post.imageUrl,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = ""
                )
            }
            Spacer(Modifier.height(LocalDPSpacing.current.lg))
            DPTextView(
                text = post.title,
                variant = DPTextViewVariant.DisplaySmallEmphasized,
                modifier = Modifier.padding()
            )
            Spacer(Modifier.height(LocalDPSpacing.current.md))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    DPFeedImage(
                        url = post.feed.websiteImageUrl,
                        initials = post.feed.initials,
                        feedTitle = post.feed.title,
                        modifier = Modifier
                            .size(LocalDPSpacing.current.lg)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically)
                    )
                    DPTextDot(Modifier.padding(horizontal = LocalDPSpacing.current.sm))
                    DPTextView(
                        text = post.feed.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        variant = DPTextViewVariant.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(16.dp))
                Row(Modifier, horizontalArrangement = Arrangement.End) {
                    DPTextView(
                        text = post.publishedText ?: post.createdAt,
                        variant = DPTextViewVariant.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DPTextDot(Modifier.padding(horizontal = LocalDPSpacing.current.sm))
                    DPTextView(
                        text = "12 Mins",
                        variant = DPTextViewVariant.BodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(LocalDPSpacing.current.sm))
        }
    }
}