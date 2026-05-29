package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconToggleButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextDot
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost

enum class FeedsPostListItemVariant {
    XS, S, M, L, XL
}

@Composable
fun FeedPostListItem(
    post: UIFeedPost,
    variant: FeedsPostListItemVariant,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
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
                    post = post,
                    variant = variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                )
            }
            FeedPostListItemFeedTitle(post, variant)
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
                        url = post.feed.websiteImageUrl,
                        initials = post.feed.initials,
                        feedTitle = post.feed.title,
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
                            post = post,
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
                FeedPostListItemBody(post, variant)
            }
        }
        FeedPostListItemMetadata(post, variant)
    }
}

@Composable
private fun FeedPostListItemMetadata(post: UIFeedPost, variant: FeedsPostListItemVariant) {
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
                    url = post.feed.websiteImageUrl,
                    initials = post.feed.initials,
                    feedTitle = post.feed.title,
                    modifier = Modifier.size(16.dp).aspectRatio(1f).clip(CircleShape)
                )
                DPTextDot()
                DPTextView(
                    text = post.feed.title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    variant = labelTextVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                DPTextDot()
            }
            DPTextView(
                text = "19 May 26",
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
        var checked by remember {
            mutableStateOf(false)
        }
        DPIconToggleButton(
            size = DPSize.Small,
            icon = DPIcons.BookmarkAddOutline,
            checkedIcon = DPIcons.BookmarkAdded,
            contentDescription = "",
            checked = checked,
            onCheckedChange = {
                checked = !checked
            }
        )
    }
}

@Composable
private fun ColumnScope.FeedPostListItemBody(post: UIFeedPost, variant: FeedsPostListItemVariant) {
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
        text = post.title,
        variant = titleTextVariant,
        maxLines = titleMaxLine,
        overflow = TextOverflow.Ellipsis
    )
    if (post.summary != null && variant != FeedsPostListItemVariant.XS) {
        DPTextView(
            text = post.summary,
            variant = summaryTextVariant,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = summaryMaxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FeedPostListItemFeedTitle(post: UIFeedPost, variant: FeedsPostListItemVariant) {
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
            url = post.feed.websiteImageUrl,
            initials = post.feed.initials,
            feedTitle = post.feed.title
        )
        DPTextDot()
        DPTextView(
            text = post.feed.title,
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
    post: UIFeedPost
) {
    val aspectRatio = remember(variant) {
        when (variant) {
            FeedsPostListItemVariant.XL -> 16f / 9f
            FeedsPostListItemVariant.L -> 21f / 9f
            else -> 1f
        }
    }
    FeedPostImage(
        article = post,
        modifier = modifier.then(Modifier.aspectRatio(aspectRatio))
    )
}