package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort

sealed interface FeedDetailScreenEvent : ScreenEvent {
    data object Retry : FeedDetailScreenEvent
    data object PinToggled : FeedDetailScreenEvent

    data class OnPostBookmarkChanged(
        val postId: String,
        val bookmarked: Boolean
    ) : FeedDetailScreenEvent

    data class OnFilterUpdated(val filter: RssPostFilter) : FeedDetailScreenEvent
    data class OnSortUpdated(val sort: UIPostSort) : FeedDetailScreenEvent
}
