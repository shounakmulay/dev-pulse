package dev.shounakmulay.devpulse.core.navigation.scene.listDetail

import androidx.compose.runtime.compositionLocalOf

data class ExpandableDetailPaneContext(
    val isExpanded: Boolean,
    val onToggleExpanded: () -> Unit
)

val LocalExpandableDetailPaneContext = compositionLocalOf<ExpandableDetailPaneContext?> { null }
