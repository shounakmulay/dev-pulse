package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface PostListScreenEvent : ScreenEvent {
    data class OnPostBookmarkChanged(
        val postId: String,
        val bookmarked: Boolean
    ) : PostListScreenEvent
}
