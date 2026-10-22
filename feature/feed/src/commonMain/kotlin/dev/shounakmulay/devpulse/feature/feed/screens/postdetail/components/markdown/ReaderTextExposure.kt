package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

internal data class ReaderExposureContext(
    val isEligible: Boolean,
    val viewport: Rect,
    val renderedContent: String,
    val selectedContent: String,
    val onVisible: () -> Unit,
)
internal val LocalReaderExposure = staticCompositionLocalOf<ReaderExposureContext?> { null }

@Composable
internal fun ReaderTextExposure(qualifies: Boolean, content: @Composable () -> Unit) {
    val exposure = LocalReaderExposure.current
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    var bounds by remember { mutableStateOf(Rect.Zero) }
    val visible = exposure != null && isReaderBodyExposed(
        renderedContent = exposure.renderedContent,
        selectedContent = exposure.selectedContent,
        hasText = qualifies,
        isEligible = exposure.isEligible,
        isResumed = lifecycleState == Lifecycle.State.RESUMED,
        isVisible = exposure.viewport.width > 0f && exposure.viewport.height > 0f &&
            bounds.width > 0f && bounds.height > 0f && bounds.overlaps(exposure.viewport),
    )
    val onVisible by rememberUpdatedState(exposure?.onVisible)
    val stillVisible by rememberUpdatedState(visible)
    LaunchedEffect(visible) {
        if (visible) {
            withFrameNanos { }
            if (stillVisible) onVisible?.invoke()
        }
    }
    Box(Modifier.onGloballyPositioned { bounds = it.boundsInWindow() }) { content() }
}
