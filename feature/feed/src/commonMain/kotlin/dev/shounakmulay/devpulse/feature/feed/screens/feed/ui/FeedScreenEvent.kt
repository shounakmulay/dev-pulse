package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed

sealed interface FeedScreenEvent : ScreenEvent {
    data class OnFeedLongClick(val feed: UIFeed) : FeedScreenEvent

    data class OnPostBookmarkChanged(
        val postId: UUID,
        val bookmarked: Boolean
    ) : FeedScreenEvent

    data class OnFeedPinChanged(
        val feedId: UUID,
        val pinned: Boolean
    ) : FeedScreenEvent

    data class OnShowFeedOptions(val feed: UIFeed) : FeedScreenEvent
    data class OnFeedOptionSelected(val feedId: UUID, val menuItem: FeedOptionsMenuItem) :
        FeedScreenEvent

    data class HideFeedOptions(val feedId: UUID) : FeedScreenEvent
    data object ConfirmDelete : FeedScreenEvent
    data object DismissDelete : FeedScreenEvent
}
