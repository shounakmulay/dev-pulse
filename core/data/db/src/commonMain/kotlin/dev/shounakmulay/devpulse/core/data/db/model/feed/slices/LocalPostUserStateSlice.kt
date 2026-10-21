package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

data class LocalPostUserStateSlice(
    val isRead: Boolean = false,
    val bookmarked: Boolean = false,
)
