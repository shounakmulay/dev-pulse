package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.ui.effect.Effect

sealed interface PostDetailScreenEffect : Effect {
    data class OpenInBrowser(val target: BrowserOpeningTarget) : PostDetailScreenEffect
}
