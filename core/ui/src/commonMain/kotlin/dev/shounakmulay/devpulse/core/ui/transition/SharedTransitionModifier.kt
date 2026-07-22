package dev.shounakmulay.devpulse.core.ui.transition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@Composable
fun Modifier.sharedElement(key: Any): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current

    return if (sharedTransitionScope != null) {
        with(sharedTransitionScope) {
            this@sharedElement.sharedElement(
                sharedContentState = sharedTransitionScope.rememberSharedContentState(key),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        this
    }
}
