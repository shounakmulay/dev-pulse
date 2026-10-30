package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface PostListScreenEvent : ScreenEvent {
    data class OpenInBrowser(val url: String) : PostListScreenEvent

    data class SetPostRead(val postId: UUID, val isRead: Boolean) : PostListScreenEvent

    data class OnSearchQueryChanged(val query: String) : PostListScreenEvent
    data class OnPostBookmarkChanged(
        val postId: UUID,
        val bookmarked: Boolean,
    ) : PostListScreenEvent
}
