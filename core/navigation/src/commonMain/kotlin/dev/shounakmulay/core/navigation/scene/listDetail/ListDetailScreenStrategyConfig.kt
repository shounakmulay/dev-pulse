package dev.shounakmulay.core.navigation.scene.listDetail

data class ListDetailScreenStrategyConfig(
    val widthBreakpoint: Int,
    val initialSplitFraction: Float,
    val expandDetail: Boolean = false
)