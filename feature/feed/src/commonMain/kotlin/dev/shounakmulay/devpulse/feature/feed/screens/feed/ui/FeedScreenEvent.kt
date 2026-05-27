package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed

sealed interface FeedScreenEvent : ScreenEvent {
    data class OnFeedLongClick(val feed: UIFeed) : FeedScreenEvent

}
