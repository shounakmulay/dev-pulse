package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost

@Composable
 fun FeedPostGridCard(
    article: UIFeedPost,
    modifier: Modifier = Modifier, // 1. Always accept a modifier for grid items
    index: Int
) {
    val showImage by remember(article, index) {
        derivedStateOf {
            article.feed.pinned || (article.imageUrl != null && index % 4 == 0)
        }
    }
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(if (showImage) 340.dp else 280.dp)
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = {},
                onLongClick = {}
            ),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceContainerHighest),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(DPTheme.spacing.md),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FeedPostGridItemImageAndHeading(showImage, article, modifier)
            Spacer(Modifier.height(LocalDPSpacing.current.md))
            FeedPostGridItemMetadata(article)
        }
    }
}