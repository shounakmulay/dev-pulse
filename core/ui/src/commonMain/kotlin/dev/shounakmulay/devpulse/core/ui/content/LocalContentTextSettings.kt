package dev.shounakmulay.devpulse.core.ui.content

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UIContentTextSettings(
    val textScale: Float = 1f,
    val lineHeightScale: Float = 1f
)

val LocalContentTextSettings = staticCompositionLocalOf<StateFlow<UIContentTextSettings>> {
    MutableStateFlow(
        UIContentTextSettings()
    )
}