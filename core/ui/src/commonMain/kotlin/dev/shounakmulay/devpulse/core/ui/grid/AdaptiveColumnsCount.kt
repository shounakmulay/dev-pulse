package dev.shounakmulay.devpulse.core.ui.grid

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_LARGE_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

@Composable
fun adaptiveColumnsCount(): Int {
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
fun BoxWithConstraintsScope.adaptiveColumnsCount(): Int {
    return remember(maxWidth) {
        when {
            maxWidth >= WIDTH_DP_EXPANDED_LOWER_BOUND.dp -> 3
            maxWidth >= WIDTH_DP_MEDIUM_LOWER_BOUND.dp -> 2
            else -> 1
        }
    }
}