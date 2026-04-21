package dev.shounakmulay.core.navigation.scene.listDetail

import androidx.window.core.layout.WindowSizeClass

object ExpandableListDetailSceneStrategyDefaults {
    val defaultConfig = ListDetailScreenStrategyConfig(
        widthBreakpoint = DEFAULT_WIDTH_BREAKPOINT,
        initialSplitFraction = 0.33f
    )

    const val DEFAULT_WIDTH_BREAKPOINT = WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
}