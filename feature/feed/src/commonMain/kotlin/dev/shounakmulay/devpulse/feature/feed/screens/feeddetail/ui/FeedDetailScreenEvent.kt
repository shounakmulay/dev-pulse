package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed

sealed interface FeedDetailScreenEvent : ScreenEvent {
    data object Retry : FeedDetailScreenEvent
    data object OnPinToggled : FeedDetailScreenEvent
    data class OnPostBookmarkChanged(
        val postId: UUID,
        val bookmarked: Boolean
    ) : FeedDetailScreenEvent


    data class OnFeedOptionSelected(
        val option: FeedOptionsMenuItem
    ) : FeedDetailScreenEvent
    data class OnShowFeedOptions(val feed: UIFeed) : FeedDetailScreenEvent
    data class HideFeedOptions(val feedId: UUID) : FeedDetailScreenEvent
    data object ConfirmDelete : FeedDetailScreenEvent
    data object DismissDelete : FeedDetailScreenEvent
}
