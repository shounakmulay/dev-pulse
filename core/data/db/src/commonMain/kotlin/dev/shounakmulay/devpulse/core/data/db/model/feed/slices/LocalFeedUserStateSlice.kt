package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

data class LocalFeedUserStateSlice(
    val pinned: Boolean = false,
    val lastOpenedAt: Long? = null,
)
