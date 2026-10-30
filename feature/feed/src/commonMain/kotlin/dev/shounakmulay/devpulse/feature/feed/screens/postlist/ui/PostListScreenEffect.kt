package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.ui.effect.Effect

sealed interface PostListScreenEffect : Effect {
    data class OpenInBrowser(val target: BrowserOpeningTarget) : PostListScreenEffect
}
