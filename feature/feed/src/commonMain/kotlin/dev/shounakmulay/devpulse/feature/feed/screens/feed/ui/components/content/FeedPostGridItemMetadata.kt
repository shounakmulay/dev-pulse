package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextDot
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost

@Composable
fun FeedPostGridItemMetadata(article: UIFeedPost) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.sm,
            Alignment.Start
        )
    ) {
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            DPFeedImage(
                url = article.feed.websiteImageUrl.orEmpty(),
                initials = article.feed.initials,
                feedTitle = article.feed.title,
                modifier = Modifier.size(14.dp).clip(MaterialTheme.shapes.large)
            )
            Spacer(Modifier.width(LocalDPSpacing.current.sm))
            DPTextView(
                modifier = Modifier.weight(1f, fill = false),
                textAlign = TextAlign.Start,
                text = article.sourceName,
                variant = DPTextViewVariant.LabelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(LocalDPSpacing.current.xs))
            DPTextDot(variant = DPTextViewVariant.BodySmall)
            Spacer(Modifier.width(LocalDPSpacing.current.xs))
            DPTextView(
                text = "19 May 26",
                variant = DPTextViewVariant.LabelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(LocalDPSpacing.current.xs))
            DPTextDot(variant = DPTextViewVariant.BodySmall)
            Spacer(Modifier.width(LocalDPSpacing.current.xs))
            DPTextView(
                text = "19 Mins",
                variant = DPTextViewVariant.LabelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DPIconButton(
            size = DPSize.Small,
            icon = Icons.Default.Bookmark,
            contentDescription = "",
            onClick = {}
        )
    }
}