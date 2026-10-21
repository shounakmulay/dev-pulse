package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface PostListScreenEvent : ScreenEvent {
    data class OnSearchQueryChanged(val query: String) : PostListScreenEvent
    data class OnPostBookmarkChanged(
        val postId: UUID,
        val bookmarked: Boolean
    ) : PostListScreenEvent
}
