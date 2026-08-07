package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface FeedDetailScreenEvent : ScreenEvent {
    data object Retry : FeedDetailScreenEvent
    data object PinToggled : FeedDetailScreenEvent

    data class OnPostBookmarkChanged(
        val postId: UUID,
        val bookmarked: Boolean
    ) : FeedDetailScreenEvent


}
