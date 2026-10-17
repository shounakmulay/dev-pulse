package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface BrowseFeedCollectionScreenEffect : Effect {
    data class ShowToast(val message: TextResource) : BrowseFeedCollectionScreenEffect

}
