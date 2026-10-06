package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface FeedListScreenEffect : Effect {
    data class Share(val text: TextResource) : FeedListScreenEffect
    data class ShowToast(val message: TextResource) : FeedListScreenEffect
}
