package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

data class LocalRssFeedIdentity(
    val id: String,
    val title: String,
    val pinned: Boolean,
    val sourceUrl: String,
    val link: String?,
    val createdAt: Long,
    val updatedAt: Long
)