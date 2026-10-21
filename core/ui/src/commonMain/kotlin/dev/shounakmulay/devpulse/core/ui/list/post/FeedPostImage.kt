package dev.shounakmulay.devpulse.core.ui.list.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import devpulse.core.resources.generated.resources.feed_article_image_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeedPostImage(
    imageUrl: String?,
    title: TextResource,
    modifier: Modifier,
) {
    if (imageUrl.isNullOrBlank()) return
    DPImage(
        url = imageUrl,
        contentDescription = stringResource(
            stringRes.feed_article_image_content_description,
            title.asString()
        ),
        modifier = modifier,
        contentScale = ContentScale.Crop,
        fallbackContent = { fallbackModifier ->
            Box(modifier = fallbackModifier, contentAlignment = Alignment.Center) {
                Image(
                    imageVector = DPIcons.ImageError,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.size(32.dp),
                )
            }
        },
    )
}

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun FeedPostImagePreview() {
    Preview {
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(Color.Gray.toArgb()) }
        ) {
            FeedPostImage(imageUrl = "preview-image", title = TextResource.fromText("Kotlin Multiplatform"), modifier = Modifier.size(240.dp, 160.dp))
        }
    }
}
