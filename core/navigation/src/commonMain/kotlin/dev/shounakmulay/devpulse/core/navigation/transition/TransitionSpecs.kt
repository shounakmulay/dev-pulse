package dev.shounakmulay.devpulse.core.navigation.transition

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

internal fun forwardTransitionSpec(): ContentTransform {
    return slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
}

internal fun backwardTransitionSpec(): ContentTransform {
    return slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
}

internal fun upwardTransitionSpec(): ContentTransform {
    return slideInVertically { it } togetherWith slideOutVertically { -it }
}

internal fun downwardTransitionSpec(): ContentTransform {
    return slideInVertically { -it } togetherWith slideOutVertically { it }
}