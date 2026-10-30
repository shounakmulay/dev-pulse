package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface FeedScreenEffect : Effect {
    data class OpenInBrowser(val target: BrowserOpeningTarget) : FeedScreenEffect

    data class FeedDeleted(val feedId: UUID) : FeedScreenEffect

    data class Share(val text: TextResource) : FeedScreenEffect
    data class ShowToast(val message: TextResource) : FeedScreenEffect
}
