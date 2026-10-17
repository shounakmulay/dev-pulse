package dev.shounakmulay.devpulse.core.ui.list

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.shounakmulay.devpulse.core.designsystem.components.DPFAB
import dev.shounakmulay.devpulse.core.designsystem.components.DPFABStyle
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import kotlinx.coroutines.launch

@Composable
fun ScrollToTopFAB(
    lazyGridState: LazyGridState,
    scrollBehavior: TopAppBarScrollBehavior,
    onScrollToTop: (suspend () -> Unit)? = null,
) = ScrollToTopFAB(
    lazyGridState = lazyGridState,
    collapsedFraction = scrollBehavior.state.collapsedFraction,
    onScrollToTop = onScrollToTop,
)

@Composable
fun ScrollToTopFAB(
    lazyGridState: LazyGridState,
    collapsedFraction: Float,
    onScrollToTop: (suspend () -> Unit)? = null,
) {
    val visible by remember(lazyGridState, collapsedFraction) {
        derivedStateOf { lazyGridState.firstVisibleItemIndex > 0 && collapsedFraction in 0f..0.5f }
    }
    val coroutineScope = rememberCoroutineScope()
    DPFAB(
        icon = DPIcons.ChevronUp,
        visible = visible,
        style = DPFABStyle.Secondary,
        onClick = {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
            if (onScrollToTop != null) {
                coroutineScope.launch {
                    onScrollToTop()
                }
            }
        }
    )
}

@DPComponentPreview
@Composable
private fun ScrollToTopFABPreview() {
    Preview {
        ScrollToTopFAB(
            lazyGridState = rememberLazyGridState(initialFirstVisibleItemIndex = 10),
            collapsedFraction = 0f,
        )
    }
}
