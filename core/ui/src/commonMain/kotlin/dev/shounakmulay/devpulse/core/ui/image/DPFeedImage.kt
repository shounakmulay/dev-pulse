package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.ColorImage
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
fun DPFeedImage(
    url: String?,
    initials: String,
    feedTitle: String,
    modifier: Modifier = Modifier
) {
    val imageDescription = stringResource(stringRes.feed_image_content_description, feedTitle)
    val initialsContent: @Composable (Modifier) -> Unit = { initialsModifier ->
        Box(
            modifier = initialsModifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clearAndSetSemantics {
                    if (url.isNullOrBlank()) contentDescription = imageDescription
                },
            contentAlignment = Alignment.Center,
        ) {
            DPTextView(
                text = initials,
                fontWeight = FontWeight.Bold,
                variant = DPTextViewVariant.LabelLarge,
            )
        }
    }
    if (url.isNullOrBlank()) {
        initialsContent(modifier)
        return
    }
    DPImage(
        modifier = modifier,
        url = url,
        contentDescription = imageDescription,
        placeholder = initialsContent,
        fallbackContent = initialsContent,
    )
}

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun DPFeedImagePreview() {
    Preview {
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(Color.Gray.toArgb()) }
        ) {
            DPFeedImage(url = null, initials = "KB", feedTitle = "Kotlin Blog", modifier = Modifier.size(64.dp))
        }
    }
}
