package dev.shounakmulay.devpulse.core.ui.grid

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_LARGE_LOWER_BOUND

@Composable
fun adaptiveRowsCount(): Int {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return remember(windowSizeClass) {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_LARGE_LOWER_BOUND) -> 3
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> 2
            else -> 1
        }
    }
}

@Composable
fun BoxWithConstraintsScope.adaptiveRowsCount(): Int {
    return remember(maxHeight) {
        when {
            maxHeight >= HEIGHT_DP_EXPANDED_LOWER_BOUND.dp -> 3
            maxHeight >= HEIGHT_DP_MEDIUM_LOWER_BOUND.dp -> 2
            else -> 1
        }
    }
}
