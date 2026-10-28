package dev.shounakmulay.devpulse.core.ui.list.post

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconToggleButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextDot
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import devpulse.core.resources.generated.resources.bookmarked
import devpulse.core.resources.generated.resources.post_read_state
import devpulse.core.resources.generated.resources.post_unread_state
import org.jetbrains.compose.resources.stringResource


@Composable
fun FeedPostListItem(
    modifier: Modifier = Modifier,
    variant: FeedsPostListItemVariant,
    showImage: Boolean,
    title: TextResource,
    description: TextResource?,
    context: TextResource?,
    imageUrl: String?,
    websiteImageUrl: String?,
    feedInitials: String,
    feedTitle: String,
    publishedText: String?,
    createdAt: String,
    bookmarked: Boolean,
    isRead: Boolean = false,
    actions: @Composable () -> Unit = {},
    onBookmarkChanged: (Boolean) -> Unit,
    onPostClick: () -> Unit
) {
    val readStateDescription = stringResource(
        if (isRead) stringRes.post_read_state else stringRes.post_unread_state
    )
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onPostClick)
            .semantics { stateDescription = readStateDescription },
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent
    ) {
        PostListItemContent(
            modifier = Modifier.padding(LocalDPSpacing.current.md),
            variant = variant,
            showImage = showImage,
            title = title,
            description = description,
            context = context,
            imageUrl = imageUrl,
            websiteImageUrl = websiteImageUrl,
            feedInitials = feedInitials,
            feedTitle = feedTitle,
            publishedText = publishedText,
            createdAt = createdAt,
            bookmarked = bookmarked,
            isRead = isRead,
            actions = actions,
            onBookmarkChanged = onBookmarkChanged
        )
    }
}

@Composable
private fun PostListItemContent(
    modifier: Modifier,
    variant: FeedsPostListItemVariant,
    showImage: Boolean,
    title: TextResource,
    description: TextResource?,
    context: TextResource?,
    imageUrl: String?,
    websiteImageUrl: String?,
    feedInitials: String,
    feedTitle: String,
    publishedText: String?,
    createdAt: String,
    bookmarked: Boolean,
    isRead: Boolean,
    actions: @Composable () -> Unit,
    onBookmarkChanged: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md)
    ) {
        if (variant > FeedsPostListItemVariant.M) {
            if (showImage) {
                FeedPostListItemImage(
                    imageUrl = imageUrl,
                    title = title,
                    variant = variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .dimmedIfRead(isRead)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
            FeedPostListItemFeedTitle(
                variant = variant,
                websiteImageUrl = websiteImageUrl,
                feedInitials = feedInitials,
                feedTitle = feedTitle,
                isRead = isRead
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.md,
                Alignment.Start
            ),
            verticalAlignment = when (variant) {
                FeedsPostListItemVariant.XS -> Alignment.CenterVertically
                else -> Alignment.Top
            }
        ) {
            when (variant) {
                FeedsPostListItemVariant.XS -> {
                    DPFeedImage(
                        url = websiteImageUrl,
                        initials = feedInitials,
                        feedTitle = feedTitle,
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .unreadFeedIconBorder(isRead)
                    )
                }

                FeedsPostListItemVariant.S,
                FeedsPostListItemVariant.M -> {
                    if (showImage) {
                        val size = if (variant == FeedsPostListItemVariant.M) {
                            88.dp
                        } else 64.dp
                        FeedPostListItemImage(
                            modifier = Modifier.size(size)
                                .dimmedIfRead(isRead)
                                .clip(MaterialTheme.shapes.medium),
                            imageUrl = imageUrl,
                            title = title,
                            variant = variant,
                        )
                    }
                }

                else -> {}
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    if (variant == FeedsPostListItemVariant.XS) {
                        LocalDPSpacing.current.xs
                    } else {
                        LocalDPSpacing.current.sm
                    }
                )
            ) {
                FeedPostListItemBody(
                    variant = variant,
                    title = title,
                    description = description,
                    context = context,
                    isRead = isRead,
                )
                if (variant == FeedsPostListItemVariant.XS) {
                    DPTextView(
                        text = publishedText ?: createdAt,
                        color = readTextColor(isRead),
                        variant = DPTextViewVariant.LabelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (variant == FeedsPostListItemVariant.XS) {
                FeedPostListItemActions(
                    bookmarked = bookmarked,
                    onBookmarkChanged = onBookmarkChanged,
                    actions = actions,
                )
            }
        }
        if (variant != FeedsPostListItemVariant.XS) {
            FeedPostListItemMetadata(
                variant = variant,
                onBookmarkChanged = onBookmarkChanged,
                websiteImageUrl = websiteImageUrl,
                feedInitials = feedInitials,
                feedTitle = feedTitle,
                publishedText = publishedText,
                createdAt = createdAt,
                bookmarked = bookmarked,
                isRead = isRead,
                actions = actions,
            )
        }
    }
}

@Composable
private fun FeedPostListItemMetadata(
    variant: FeedsPostListItemVariant,
    websiteImageUrl: String?,
    feedInitials: String,
    feedTitle: String,
    publishedText: String?,
    createdAt: String,
    bookmarked: Boolean,
    actions: @Composable () -> Unit,
    onBookmarkChanged: (Boolean) -> Unit,
    isRead: Boolean
) {
    val labelTextVariant = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> DPTextViewVariant.LabelSmall
            FeedsPostListItemVariant.S -> DPTextViewVariant.LabelSmall
            FeedsPostListItemVariant.M -> DPTextViewVariant.LabelSmall
            FeedsPostListItemVariant.L -> DPTextViewVariant.LabelMedium
            FeedsPostListItemVariant.XL -> DPTextViewVariant.LabelMedium
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.xs,
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.xs,
            ),
            modifier = Modifier.weight(1f)
        ) {
            if (variant in listOf(FeedsPostListItemVariant.M, FeedsPostListItemVariant.S)) {
                DPFeedImage(
                    url = websiteImageUrl,
                    initials = feedInitials,
                    feedTitle = feedTitle,
                    modifier = Modifier
                        .size(16.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .unreadFeedIconBorder(isRead)
                        .dimmedIfRead(isRead)
                )
                DPTextDot()
                DPTextView(
                    text = feedTitle,
                    color = readTextColor(isRead),
                    variant = labelTextVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                DPTextDot()
            }
            DPTextView(
                text = publishedText ?: createdAt,
                color = readTextColor(isRead),
                variant = labelTextVariant,
                maxLines = 1
            )
        }
        FeedPostListItemActions(
            bookmarked = bookmarked,
            onBookmarkChanged = onBookmarkChanged,
            actions = actions,
        )
    }
}

@Composable
private fun FeedPostListItemActions(
    bookmarked: Boolean,
    onBookmarkChanged: (Boolean) -> Unit,
    actions: @Composable () -> Unit,
) {
    DPIconToggleButton(
        size = DPSize.Small,
        icon = DPIcons.BookmarkAddOutline,
        checkedIcon = DPIcons.BookmarkAdded,
        contentDescription = stringResource(stringRes.bookmarked),
        checked = bookmarked,
        onCheckedChange = onBookmarkChanged,
    )
    actions()
}

@Composable
private fun ColumnScope.FeedPostListItemBody(
    variant: FeedsPostListItemVariant,
    title: TextResource,
    description: TextResource?,
    context: TextResource?,
    isRead: Boolean,
) {
    val titleStyle = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> DPTextViewVariant.TitleSmall
            FeedsPostListItemVariant.S -> DPTextViewVariant.TitleMedium
            FeedsPostListItemVariant.M -> DPTextViewVariant.TitleMedium
            FeedsPostListItemVariant.L -> DPTextViewVariant.TitleMedium
            FeedsPostListItemVariant.XL -> DPTextViewVariant.TitleLarge
        }
    }
    val titleMaxLine = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> 1
            FeedsPostListItemVariant.S -> 2
            FeedsPostListItemVariant.M -> 2
            FeedsPostListItemVariant.L -> 2
            FeedsPostListItemVariant.XL -> 3
        }
    }
    val summaryTextVariant = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> DPTextViewVariant.BodySmall
            FeedsPostListItemVariant.S -> DPTextViewVariant.BodyMedium
            FeedsPostListItemVariant.M -> DPTextViewVariant.BodyMedium
            FeedsPostListItemVariant.L -> DPTextViewVariant.BodyMedium
            FeedsPostListItemVariant.XL -> DPTextViewVariant.BodyLarge
        }
    }
    val summaryMaxLines = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> 0
            FeedsPostListItemVariant.S -> 1
            FeedsPostListItemVariant.M -> 2
            FeedsPostListItemVariant.L -> 2
            FeedsPostListItemVariant.XL -> 3
        }
    }

    DPTextView(
        text = title.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
        color = readTextColor(isRead, ifNotRead = MaterialTheme.colorScheme.onSurface),
        variant = titleStyle,
        fontWeight = if (isRead) FontWeight.Normal else null,
        maxLines = titleMaxLine,
        overflow = TextOverflow.Ellipsis
    )
    val summary = context ?: description
    if (summary != null && variant != FeedsPostListItemVariant.XS) {
        DPTextView(
            text = summary.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
            variant = summaryTextVariant,
            color = readTextColor(isRead),
            maxLines = summaryMaxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FeedPostListItemFeedTitle(
    variant: FeedsPostListItemVariant,
    websiteImageUrl: String?,
    feedInitials: String,
    feedTitle: String,
    isRead: Boolean
) {
    val imageSize = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XL -> 20.dp
            FeedsPostListItemVariant.L -> 18.dp
            else -> 16.dp
        }
    }
    val textVariant = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XL -> DPTextViewVariant.LabelMedium
            else -> DPTextViewVariant.LabelSmall
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.sm,
            Alignment.Start
        )
    ) {
        DPFeedImage(
            modifier = Modifier.size(imageSize).clip(CircleShape)
                .unreadFeedIconBorder(isRead),
            url = websiteImageUrl,
            initials = feedInitials,
            feedTitle = feedTitle
        )
        DPTextDot()
        DPTextView(
            text = feedTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            variant = textVariant,
            fontWeight = if (isRead) FontWeight.Medium else null,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FeedPostListItemImage(
    modifier: Modifier,
    variant: FeedsPostListItemVariant,
    imageUrl: String?,
    title: TextResource
) {
    val aspectRatio = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XL -> 16f / 9f
            FeedsPostListItemVariant.L -> 21f / 9f
            else -> 1f
        }
    }
    FeedPostImage(
        modifier = modifier.then(Modifier.aspectRatio(aspectRatio)),
        imageUrl = imageUrl,
        title = title
    )
}

@Composable
private fun Modifier.unreadFeedIconBorder(isRead: Boolean): Modifier {
    return if (isRead) this else border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    )
}

private fun Modifier.dimmedIfRead(isRead: Boolean): Modifier {
    return this.alpha(if (isRead) 0.25f else 1f)
}

@Composable
private fun readTextColor(
    isRead: Boolean,
    ifNotRead: Color = MaterialTheme.colorScheme.onSurfaceVariant
): Color {
    return if (isRead) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        ifNotRead
    }

}

@DPScreenPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun FeedPostListItemVariantsPreview() {
    Preview {
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler {
                ColorImage(Color(0xFFB8ADA1).toArgb())
            }
        ) {
            LazyColumn(
                contentPadding = PaddingValues(LocalDPSpacing.current.lg),
                verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
            ) {
                items(FeedsPostListItemVariant.entries.drop(3).flatMap { variant ->
                    listOf(variant to false, variant to true)
                }) { (variant, isRead) ->
                    FeedPostListItem(
                        variant = variant,
                        showImage = true,
                        title = TextResource.fromText("Building better apps with Kotlin Multiplatform"),
                        description = TextResource.fromText("Shared business logic and adaptive interfaces across platforms."),
                        context = null,
                        imageUrl = "preview-article",
                        websiteImageUrl = null,
                        feedInitials = "KB",
                        feedTitle = "Kotlin Blog",
                        publishedText = "2 hours ago",
                        createdAt = "Today",
                        bookmarked = variant == FeedsPostListItemVariant.M,
                        isRead = isRead,
                        onBookmarkChanged = {},
                        onPostClick = {},
                    )
                }
            }
        }
    }
}
