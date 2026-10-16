package dev.shounakmulay.devpulse.feature.feed.components.feed

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage

@Composable
fun DPFeedListItemImage(
    modifier: Modifier = Modifier,
    url: String?,
    initials: String,
    feedTitle: String
) {
    Surface(
        modifier = modifier
            .size(LocalDPSpacing.current.listItemHeight)
            .clip(MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.25f),
    ) {
        DPFeedImage(
            url = url.orEmpty(),
            initials = initials,
            feedTitle = feedTitle
        )
    }
}