package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

data class LocalRssContentFeedPostIdentitySlice(
    val id: String,
    val fingerprint: String,
    val bookmarked: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
