package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import dev.shounakmulay.devpulse.core.ui.effect.Effect

sealed interface AddFeedScreenEffect : Effect {

    data class SetAddOpmlBottomSheetVisible(val visible: Boolean) : AddFeedScreenEffect
}