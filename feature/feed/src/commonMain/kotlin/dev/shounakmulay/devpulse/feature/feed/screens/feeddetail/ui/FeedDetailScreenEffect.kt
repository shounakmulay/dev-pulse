package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface FeedDetailScreenEffect : Effect {
    data object NavigateBack : FeedDetailScreenEffect
    data class ShowToast(val message: TextResource) : FeedDetailScreenEffect
    data class Share(val text: TextResource) : FeedDetailScreenEffect
}
