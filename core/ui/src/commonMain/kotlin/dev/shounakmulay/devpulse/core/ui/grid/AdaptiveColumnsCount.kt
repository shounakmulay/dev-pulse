package dev.shounakmulay.devpulse.core.ui.grid

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass

@Composable
fun adaptiveColumnsCount(): GridCells {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return remember(windowSizeClass) {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> GridCells.Fixed(
                2
            )

            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND) -> GridCells.Fixed(
                3
            )

            else -> GridCells.Fixed(1)
        }
    }
}