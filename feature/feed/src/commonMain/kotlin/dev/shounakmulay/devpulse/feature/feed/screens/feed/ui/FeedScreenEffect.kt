package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface FeedScreenEffect : Effect {
    data class Share(val text: TextResource) : FeedScreenEffect
    data class ShowToast(val message: TextResource) : FeedScreenEffect
}
