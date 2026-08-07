package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.mikepenz.markdown.model.ImageData
import com.mikepenz.markdown.model.ImageTransformer

internal object CoilMarkdownTransformer : ImageTransformer {

    @Composable
    override fun transform(link: String): ImageData? {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(link)
                .size(coil3.size.Size.ORIGINAL)
                .build(),
        )
        return ImageData(
            painter = painter,
            modifier = Modifier
                .clip(MaterialTheme.shapes.large),
            alignment = Alignment.Center,
        )
    }

    // Kept for compatibility with the interface / any block-image call sites that still
    // hit the default library path (e.g. genuinely standalone images with no split needed).
    // The split-paragraph image branch below does NOT call this — it drives sizing directly
    // off AsyncImagePainter.state instead, which is more precise (see rationale below).
    @Composable
    override fun intrinsicSize(painter: Painter): Size {
        var size by remember(painter) { mutableStateOf(painter.intrinsicSize) }
        if (painter is AsyncImagePainter) {
            val state by painter.state.collectAsState()
            (state as? AsyncImagePainter.State.Success)?.painter?.intrinsicSize?.let { size = it }
        }
        return size
    }
}