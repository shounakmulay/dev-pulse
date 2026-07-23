package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed

sealed interface FeedScreenEvent : ScreenEvent {
    data class OnFeedLongClick(val feed: UIFeed) : FeedScreenEvent

    data class OnPostBookmarkChanged(
        val postId: String,
        val bookmarked: Boolean
    ) : FeedScreenEvent

    data class OnFeedPinChanged(
        val feedId: String,
        val pinned: Boolean
    ): FeedScreenEvent
}
