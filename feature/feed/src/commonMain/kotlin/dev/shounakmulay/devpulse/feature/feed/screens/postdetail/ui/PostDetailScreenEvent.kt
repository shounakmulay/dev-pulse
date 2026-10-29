package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection

sealed interface PostDetailScreenEvent : ScreenEvent {
    data class OpenInBrowser(val url: String) : PostDetailScreenEvent
    data object ContentAvailable : PostDetailScreenEvent
    data class SetPostRead(val postId: UUID, val isRead: Boolean) : PostDetailScreenEvent
    data class SetPostBookmarked(val bookmarked: Boolean) : PostDetailScreenEvent
    data class OnSectionSelected(val section: PostDetailScreenSection) : PostDetailScreenEvent
    data class SetContentTextScale(val scale: Float) : PostDetailScreenEvent
    data class SetContentLineHeightScale(val scale: Float) : PostDetailScreenEvent
}
