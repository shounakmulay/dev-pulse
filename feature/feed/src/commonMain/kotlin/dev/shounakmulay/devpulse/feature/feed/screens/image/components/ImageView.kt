package dev.shounakmulay.devpulse.feature.feed.screens.image.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.modifier.ifElse
import dev.shounakmulay.devpulse.core.ui.transition.sharedBounds
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.roundToInt

@Composable
internal fun ImageView(
    url: String,
    isDragging: Boolean,
    scaffoldPadding: PaddingValues,
    zoomableState: ZoomState,
    offsetY: Animatable<Float, AnimationVector1D>,
) {
    val platformContext = LocalPlatformContext.current
    val imageRequest = remember(platformContext, url) {
        ImageRequest.Builder(platformContext)
            .data(url)
            .build()
    }
    val imageLoader = remember(platformContext) {
        ImageLoader.Builder(platformContext)
            .components {
                add(SvgDecoder.Factory())
            }.build()
    }
    val padding by animateDpAsState(
        if (isDragging) LocalDPSpacing.current.lg else 0.dp
    )
    val clipCornerRadius by animateDpAsState(
        if (isDragging || offsetY.value > 200) LocalDPSpacing.current.lg else 0.dp
    )
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWidthLarger = remember(windowSizeClass) {
        windowSizeClass.minWidthDp > windowSizeClass.minHeightDp
    }
    Box(
        Modifier
            .fillMaxSize()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .padding(scaffoldPadding)
            .zoomable(zoomableState)
    ) {
        AsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(padding)
                .ifElse(
                    condition = isWidthLarger,
                    ifTrue = {
                        this.fillMaxHeight().wrapContentWidth()
                    },
                    ifFalse = {
                        this.fillMaxWidth().wrapContentHeight()
                    }
                )
                .clip(RoundedCornerShape(clipCornerRadius))
                .sharedBounds("Image$url", MaterialTheme.shapes.large),
            model = imageRequest,
            contentDescription = "",
            imageLoader = imageLoader,
            contentScale = ContentScale.Fit,
            placeholder = rememberVectorPainter(DPIcons.ImageLoading),
            fallback = rememberVectorPainter(DPIcons.ImageError),
            error = rememberVectorPainter(DPIcons.ImageError),
            onSuccess = {
                zoomableState.setContentSize(it.painter.intrinsicSize)
            }
        )
    }
}