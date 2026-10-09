package dev.shounakmulay.devpulse.core.ui.list.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconToggleButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextDot
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString


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
    onBookmarkChanged: (Boolean) -> Unit,
    onPostClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onPostClick,
                onLongClick = {},
            ),
        border = BorderStroke(
            1.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.25f)
        ),
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
    onBookmarkChanged: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.md,
            Alignment.CenterVertically
        )
    ) {
        if (variant > FeedsPostListItemVariant.M) {
            if (showImage) {
                FeedPostListItemImage(
                    imageUrl = imageUrl,
                    title = title,
                    variant = variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                )
            }
            FeedPostListItemFeedTitle(
                variant = variant,
                websiteImageUrl = websiteImageUrl,
                feedInitials = feedInitials,
                feedTitle = feedTitle
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.md,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (variant) {
                FeedsPostListItemVariant.XS -> {
                    DPFeedImage(
                        url = websiteImageUrl,
                        initials = feedInitials,
                        feedTitle = feedTitle,
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                    )
                }

                FeedsPostListItemVariant.S,
                FeedsPostListItemVariant.M -> {
                    if (showImage) {
                        val size = if (variant == FeedsPostListItemVariant.M) {
                            80.dp
                        } else 64.dp
                        FeedPostListItemImage(
                            modifier = Modifier.size(size).clip(MaterialTheme.shapes.medium),
                            imageUrl = imageUrl,
                            title = title,
                            variant = variant,
                        )
                    }
                }

                else -> {}
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    LocalDPSpacing.current.md,
                    Alignment.CenterVertically
                )
            ) {
                FeedPostListItemBody(
                    variant = variant,
                    title = title,
                    description = description,
                    context = context
                )
            }
        }
        FeedPostListItemMetadata(
            variant = variant,
            onBookmarkChanged = onBookmarkChanged,
            websiteImageUrl = websiteImageUrl,
            feedInitials = feedInitials,
            feedTitle = feedTitle,
            publishedText = publishedText,
            createdAt = createdAt,
            bookmarked = bookmarked
        )
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
    onBookmarkChanged: (Boolean) -> Unit
) {
    val labelTextVariant = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> DPTextViewVariant.LabelSmall
            FeedsPostListItemVariant.S -> DPTextViewVariant.LabelSmall
            FeedsPostListItemVariant.M -> DPTextViewVariant.LabelSmallEmphasized
            FeedsPostListItemVariant.L -> DPTextViewVariant.LabelMedium
            FeedsPostListItemVariant.XL -> DPTextViewVariant.LabelMediumEmphasized
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
                    modifier = Modifier.size(16.dp).aspectRatio(1f).clip(CircleShape)
                )
                DPTextDot()
                DPTextView(
                    text = feedTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    variant = labelTextVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                DPTextDot()
            }
            DPTextView(
                text = publishedText ?: createdAt,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                variant = labelTextVariant,
                maxLines = 1
            )
            DPTextDot()
            DPTextView(
                text = "12 Mins",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                variant = labelTextVariant,
                maxLines = 1
            )
        }
        DPIconToggleButton(
            size = DPSize.Small,
            icon = DPIcons.BookmarkAddOutline,
            checkedIcon = DPIcons.BookmarkAdded,
            contentDescription = "",
            checked = bookmarked,
            onCheckedChange = { checked ->
                onBookmarkChanged(checked)
            },
        )
    }
}

@Composable
private fun FeedPostListItemBody(
    variant: FeedsPostListItemVariant,
    title: TextResource,
    description: TextResource?,
    context: TextResource?
) {
    val titleTextVariant = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XS -> DPTextViewVariant.TitleSmall
            FeedsPostListItemVariant.S -> DPTextViewVariant.TitleSmallEmphasized
            FeedsPostListItemVariant.M -> DPTextViewVariant.TitleMedium
            FeedsPostListItemVariant.L -> DPTextViewVariant.TitleLargeEmphasized
            FeedsPostListItemVariant.XL -> DPTextViewVariant.HeadingSmallEmphasized
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
            FeedsPostListItemVariant.S -> DPTextViewVariant.BodySmall
            FeedsPostListItemVariant.M -> DPTextViewVariant.BodySmallEmphasized
            FeedsPostListItemVariant.L -> DPTextViewVariant.BodyMedium
            FeedsPostListItemVariant.XL -> DPTextViewVariant.BodyMediumEmphasized
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
        variant = titleTextVariant,
        maxLines = titleMaxLine,
        overflow = TextOverflow.Ellipsis
    )
    val summary = context ?: description
    if (summary != null && variant != FeedsPostListItemVariant.XS) {
        DPTextView(
            text = summary.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
            variant = summaryTextVariant,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    feedTitle: String
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
            modifier = Modifier.size(imageSize).clip(CircleShape),
            url = websiteImageUrl,
            initials = feedInitials,
            feedTitle = feedTitle
        )
        DPTextDot()
        DPTextView(
            text = feedTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            variant = textVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
