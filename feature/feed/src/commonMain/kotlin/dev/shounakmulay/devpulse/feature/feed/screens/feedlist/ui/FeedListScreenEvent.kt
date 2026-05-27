package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab

sealed interface FeedListScreenEvent : ScreenEvent {
    data class SelectTab(val tab: UISelectedTab): FeedListScreenEvent
    data class TogglePinned(val id: String, val pinned: Boolean) : FeedListScreenEvent
}
