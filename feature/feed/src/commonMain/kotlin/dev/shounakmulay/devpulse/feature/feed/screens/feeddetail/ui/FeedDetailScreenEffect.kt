package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.text.TextResource

sealed interface FeedDetailScreenEffect : Effect {
    data class OpenInBrowser(val target: BrowserOpeningTarget) : FeedDetailScreenEffect

    data class FeedDeleted(val feedId: UUID) : FeedDetailScreenEffect
    data class ShowToast(val message: TextResource) : FeedDetailScreenEffect
    data class Share(val text: TextResource) : FeedDetailScreenEffect
}
