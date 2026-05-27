package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

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