package dev.shounakmulay.devpulse.feature.feed.components.feed

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
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

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun DPFeedListItemImagePreview() {
    Preview {
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(Color.Gray.toArgb()) }
        ) {
            DPFeedListItemImage(url = null, initials = "KB", feedTitle = "Kotlin Blog")
        }
    }
}
