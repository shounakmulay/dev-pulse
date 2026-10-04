package dev.shounakmulay.devpulse.feature.feed.screens.feedsearch.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface FeedSearchScreenEvent : ScreenEvent {
    data class OnSearchQueryChanged(val query: String) : FeedSearchScreenEvent
}
