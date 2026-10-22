package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.request.ErrorResult
import com.mikepenz.markdown.compose.LocalReferenceLinkHandler
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.core.ui.transition.sharedBounds
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.CancellationToken
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun MDImage(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    onImageClick: (link: String) -> Unit
) {
    val link = node.resolveImageLink(
        content,
        LocalReferenceLinkHandler.current
    )
    val alt = node.resolveImageAlt(content)
    if (link != null) {
        var aspectRatio by rememberSaveable(link) { mutableStateOf(16f / 9f) }

        DPImage(
            url = link,
            contentDescription = alt.orEmpty(),
            modifier = modifier
                .aspectRatio(aspectRatio)
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    onImageClick(link)
                }
                .sharedBounds("Image$link", MaterialTheme.shapes.large),
            contentScale = ContentScale.Fit,
            placeholder = { placeholderModifier ->
                MDImagePlaceholder(modifier = placeholderModifier, icon = DPIcons.ImageLoading)
            },
            fallbackContent = { fallbackModifier ->
                MDImagePlaceholder(modifier = fallbackModifier, icon = DPIcons.ImageError)
            },
            onSuccess = { state ->
                val intrinsic = state.painter.intrinsicSize
                if (intrinsic.isSpecified && intrinsic.height > 0f) {
                    aspectRatio = intrinsic.width / intrinsic.height
                }
            },
        )
    }
}

@Composable
private fun MDImagePlaceholder(modifier: Modifier, icon: ImageVector) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )
    }
}

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun MDImageLoadingPreview() {
    MDImageStatePreview(
        handler = AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(painter = null) }
    )
}

@DPComponentPreview
@Composable
@OptIn(coil3.annotation.ExperimentalCoilApi::class)
private fun MDImageErrorPreview() {
    MDImageStatePreview(
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
private fun MDImageStatePreview(handler: AsyncImagePreviewHandler) {
    Preview {
        AnimatedContent(targetState = "![Article illustration](preview-image)", label = "Article image preview") { content ->
            val node = remember(content) {
                val markdown: CharSequence = content
                checkNotNull(
                    MarkdownParser(
                        flavour = GFMFlavourDescriptor(),
                        cancellationToken = CancellationToken.NonCancellable,
                    )
                        .buildMarkdownTreeFromString(markdown)
                        .findChildOfTypeRecursive(MarkdownElementTypes.IMAGE)
                )
            }
            CompositionLocalProvider(
                LocalAsyncImagePreviewHandler provides handler,
                LocalNavAnimatedContentScope provides this,
            ) {
                MDImage(content = content, node = node, modifier = Modifier.width(320.dp), onImageClick = {})
            }
        }
    }
}
