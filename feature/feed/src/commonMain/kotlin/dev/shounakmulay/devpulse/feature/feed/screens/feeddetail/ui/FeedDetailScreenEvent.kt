package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface FeedDetailScreenEvent : ScreenEvent {
    data object Retry : FeedDetailScreenEvent
}
