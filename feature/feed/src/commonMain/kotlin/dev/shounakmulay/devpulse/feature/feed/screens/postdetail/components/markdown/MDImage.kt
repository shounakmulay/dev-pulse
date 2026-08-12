package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import coil3.svg.SvgDecoder
import com.mikepenz.markdown.compose.LocalReferenceLinkHandler
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import org.intellij.markdown.ast.ASTNode

@Composable
fun MDImage(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
) {
    val link = node.resolveImageLink(
        content,
        LocalReferenceLinkHandler.current
    )
    val alt = node.resolveImageAlt(content)
    if (link != null) {
        var aspectRatio by rememberSaveable(link) { mutableStateOf(16f / 9f) }

        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(link)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = alt,
            imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
                .components {
                    add(SvgDecoder.Factory())
                }.build(),
            modifier = modifier
                .aspectRatio(aspectRatio)
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Fit,
            placeholder = rememberVectorPainter(DPIcons.ImageLoading),
            fallback = rememberVectorPainter(DPIcons.ImageError),
            error = rememberVectorPainter(DPIcons.ImageError),
            onSuccess = { state ->
                val intrinsic = state.painter.intrinsicSize
                if (intrinsic.isSpecified && intrinsic.height > 0f) {
                    aspectRatio = intrinsic.width / intrinsic.height
                }
            },
        )
    }
}
