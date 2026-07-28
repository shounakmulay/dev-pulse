package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort

sealed interface PostListScreenEvent : ScreenEvent {
    data class OnPostBookmarkChanged(
        val postId: String,
        val bookmarked: Boolean
    ) : PostListScreenEvent
}
