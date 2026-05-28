package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
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

@Composable
fun DPFeedImage(
    url: String?,
    initials: String,
    feedTitle: String,
    modifier: Modifier = Modifier
) {
    DPImage(
        modifier = modifier.fillMaxSize(),
        url = url.orEmpty(),
        contentDescription = stringResource(stringRes.feed_image_content_description, feedTitle),
        fallbackContent = { fallbackModifier ->
            Box(
                modifier = fallbackModifier,
                contentAlignment = Alignment.Center,
            ) {
                DPTextView(
                    text = initials,
                    fontWeight = FontWeight.Bold,
                    variant = DPTextViewVariant.LabelLarge,
                )
            }
        },
    )
}