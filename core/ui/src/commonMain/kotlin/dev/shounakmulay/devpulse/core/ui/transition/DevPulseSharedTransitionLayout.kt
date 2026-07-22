package dev.shounakmulay.devpulse.core.ui.transition

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun DevPulseSharedTransitionLayout(content: @Composable SharedTransitionScope.() -> Unit) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            content()
        }
    }
}

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }