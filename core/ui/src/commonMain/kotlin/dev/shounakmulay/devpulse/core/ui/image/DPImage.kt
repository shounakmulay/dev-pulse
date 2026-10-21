package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.feed_image_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun DPImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    placeholder: @Composable (modifier: Modifier) -> Unit = {},
    fallbackContent: @Composable (modifier: Modifier) -> Unit = {}
) {
    var showErrorFallback by remember(url) {
        mutableStateOf(false)
    }
    var isLoading by remember(url) {
        mutableStateOf(true)
    }

    if (showErrorFallback) {
        fallbackContent(modifier)
    }

    if (!showErrorFallback) {
        AsyncImage(
            modifier = modifier,
            model = url,
            contentDescription = contentDescription,
            contentScale = contentScale,
            alignment = alignment,
            onLoading = {
                isLoading = true
            },
            onSuccess = {
                isLoading = false
            },
            onError = {
                showErrorFallback = true
                isLoading = false
            },
        )
    }

    if (isLoading && !showErrorFallback) {
        placeholder(modifier)
    }
}

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun DPImagePreview() {
    Preview {
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(Color.Gray.toArgb()) }
        ) {
            DPImage(
                url = "preview-image",
                contentDescription = "Article image",
                modifier = Modifier.size(240.dp, 160.dp),
            )
        }
    }
}
