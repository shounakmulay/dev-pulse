package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface AddFeedScreenEvent : ScreenEvent {
    data class UpdateUrl(val index: Int, val url: String) : AddFeedScreenEvent

    data object ImportFeeds : AddFeedScreenEvent
}
