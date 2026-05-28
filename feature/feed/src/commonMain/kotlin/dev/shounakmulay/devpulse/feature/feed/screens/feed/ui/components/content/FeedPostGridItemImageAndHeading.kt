package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost

@Composable
fun FeedPostGridItemImageAndHeading(
    showImage: Boolean,
    article: UIFeedPost,
    modifier: Modifier
) {
    Row(
        modifier = Modifier.wrapContentHeight(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md)
    ) {
        if (showImage) {
            ArticleImage(
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium),
                article = article,
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.sm,
                Alignment.CenterVertically
            )
        ) {
            DPTextView(
                text = article.title,
                maxLines = 2,
                variant = DPTextViewVariant.TitleMediumEmphasized,
                overflow = TextOverflow.Ellipsis
            )
            article.summary?.takeIf { it.isNotBlank() }?.let {
                DPTextView(
                    modifier = modifier,
                    text = it,
                    maxLines = 2,
                    variant = DPTextViewVariant.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}