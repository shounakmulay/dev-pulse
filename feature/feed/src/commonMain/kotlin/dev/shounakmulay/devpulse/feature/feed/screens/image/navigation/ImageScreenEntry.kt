package dev.shounakmulay.devpulse.feature.feed.screens.image.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.image.ImageScreen

internal fun EntryProviderScope<Screen>.imageScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.ImageScreen>(
        metadata = NavDisplay.transitionSpec {
            // Forward navigation: Zoom up from 85% size + Fade in
            (scaleIn(initialScale = 0.85f, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(300))) togetherWith
                    ExitTransition.KeepUntilTransitionsFinished
        } + NavDisplay.popTransitionSpec {
            // Backward navigation: Zoom down to 85% size + Fade out
            EnterTransition.None togetherWith
                    (scaleOut(targetScale = 0.85f, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300)))
        } + NavDisplay.predictivePopTransitionSpec {
            EnterTransition.None togetherWith
                    (scaleOut(targetScale = 0.85f, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300)))
        }
    ) {
        ImageScreen(url = it.url, navigator = navigator)
    }
}