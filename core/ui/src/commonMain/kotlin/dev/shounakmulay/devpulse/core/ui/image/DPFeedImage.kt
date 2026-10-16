package dev.shounakmulay.devpulse.core.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
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