package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface AddFeedScreenEvent : ScreenEvent {
    data class UpdateUrl(val id: String, val url: String) : AddFeedScreenEvent
    data class UpdateName(val id: String, val name: String) : AddFeedScreenEvent
    data class Delete(val id: String) : AddFeedScreenEvent
    data object Add : AddFeedScreenEvent

    data object ImportFeeds : AddFeedScreenEvent
    data class ToggleExpanded(val id: String) : AddFeedScreenEvent
    data class ValidateSourceUrl(val id: String) : AddFeedScreenEvent
}
