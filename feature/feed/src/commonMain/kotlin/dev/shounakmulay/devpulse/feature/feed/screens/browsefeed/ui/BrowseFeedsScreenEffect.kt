package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface BrowseFeedsScreenEffect : Effect {
    data class ShowToast(val message: TextResource) : BrowseFeedsScreenEffect
    data class OpenCollection(val collectionId: UUID) : BrowseFeedsScreenEffect
}
