package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview

private enum class DPImageState { Loading, Loaded, Error }

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
    var state by remember(url) {
        mutableStateOf(DPImageState.Loading)
    }

    Box(
        modifier = modifier,
        contentAlignment = alignment,
        propagateMinConstraints = true,
    ) {
        AsyncImage(
            model = url,
            contentDescription = contentDescription.takeIf { state == DPImageState.Loaded },
            contentScale = contentScale,
            alignment = alignment,
            onLoading = {
                state = DPImageState.Loading
            },
            onSuccess = {
                state = DPImageState.Loaded
            },
            onError = {
                state = DPImageState.Error
            },
        )
        if (state != DPImageState.Loaded) {
            Box(
                modifier = Modifier.matchParentSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
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
