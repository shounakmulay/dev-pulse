package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons

private enum class DPImageState { Loading, Loaded, Error }

@Composable
fun DPImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    onSuccess: (AsyncImagePainter.State.Success) -> Unit = {},
    placeholder: @Composable (modifier: Modifier) -> Unit = {},
    fallbackContent: @Composable (modifier: Modifier) -> Unit = {}
) {
    val context = LocalPlatformContext.current
    val request = remember(context, url) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(120)
            .build()
    }
    var state by remember(url) {
        mutableStateOf(DPImageState.Loading)
    }

    Box(
        modifier = modifier,
        contentAlignment = alignment,
        propagateMinConstraints = true,
    ) {
        AsyncImage(
            model = request,
            contentDescription = contentDescription.takeIf { state == DPImageState.Loaded },
            contentScale = contentScale,
            alignment = alignment,
            onLoading = {
                state = DPImageState.Loading
            },
            onSuccess = { result ->
                state = DPImageState.Loaded
                onSuccess(result)
            },
            onError = {
                state = DPImageState.Error
            },
        )
        if (state != DPImageState.Loaded) {
            Box(
                modifier = Modifier.matchParentSize()
                    .semantics { this.contentDescription = contentDescription },
                contentAlignment = alignment,
            ) {
                when (state) {
                    DPImageState.Loading -> placeholder(Modifier.fillMaxSize())
                    DPImageState.Error -> fallbackContent(Modifier.fillMaxSize())
                    DPImageState.Loaded -> Unit
                }
            }
        }
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

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun DPImageLoadingPreview() {
    DPImageStatePreview(
        handler = AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(painter = null) }
    )
}

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun DPImageErrorPreview() {
    DPImageStatePreview(
        handler = AsyncImagePreviewHandler { _, request ->
            AsyncImagePainter.State.Error(
                painter = null,
                result = ErrorResult(image = null, request = request, throwable = IllegalStateException("Preview image error")),
            )
        }
    )
}

@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun DPImageStatePreview(handler: AsyncImagePreviewHandler) {
    Preview {
        CompositionLocalProvider(LocalAsyncImagePreviewHandler provides handler) {
            DPImage(
                url = "preview-image",
                contentDescription = "Article image",
                modifier = Modifier.size(240.dp, 160.dp),
                placeholder = { DPImagePreviewPlaceholder(modifier = it, icon = DPIcons.ImageLoading) },
                fallbackContent = { DPImagePreviewPlaceholder(modifier = it, icon = DPIcons.ImageError) },
            )
        }
    }
}

@Composable
private fun DPImagePreviewPlaceholder(modifier: Modifier, icon: ImageVector) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )
    }
}
