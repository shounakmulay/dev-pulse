package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import devpulse.core.resources.generated.resources.feed_image_content_description
import devpulse.core.resources.generated.resources.feed_pinned_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun LazyGridItemScope.FeedsGridItem(
    imageUrl: String?,
    title: String,
    initials: String,
    pinned: Boolean,
    sourceUrl: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val spacing = LocalDPSpacing.current
    Surface(
        Modifier
            .width(90.dp)
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .animateItem(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.listItemHeight)
                    .padding(spacing.sm),
            ) {
                FeedsGridItemImage(imageUrl = imageUrl, initials = initials, title = title)
                if (pinned) {
                    PinnedIcon()
                }
            }
            ListItemLabel(title = title, sourceUrl = sourceUrl)
        }
    }
}

@Composable
private fun FeedsGridItemImage(
    imageUrl: String?,
    initials: String,
    title: String,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Box(contentAlignment = Alignment.Center) {
            DPImage(
                modifier = Modifier.fillMaxSize(0.8f),
                url = imageUrl.orEmpty(),
                contentDescription = stringResource(
                    stringRes.feed_image_content_description,
                    title
                ),
                fallbackContent = {
                    FeedInitialsText(initials)
                }
            )
        }
    }
}

@Composable
private fun FeedInitialsText(initials: String) {
    DPTextView(
        modifier = Modifier
            .wrapContentHeight(),
        text = initials,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        autoSize = TextAutoSize.StepBased(
            minFontSize = 12.sp,
            maxFontSize = 18.sp
        ),
        fontWeight = FontWeight.Bold,
        variant = DPTextViewVariant.DisplaySmallEmphasized,
    )
}

@Composable
private fun BoxScope.PinnedIcon() {
    Surface(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(18.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.tertiary,
    ) {
        Icon(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize(),
            imageVector = DPIcons.Pin,
            contentDescription = stringResource(stringRes.feed_pinned_content_description),
            tint = MaterialTheme.colorScheme.onTertiary,
        )
    }
}

@Composable
private fun ListItemLabel(title: String?, sourceUrl: String) {
    DPTextView(
        modifier = Modifier.padding(LocalDPSpacing.current.xs),
        text = title ?: sourceUrl,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        variant = DPTextViewVariant.LabelSmall
    )
}
