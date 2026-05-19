package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

data class LocalRssContentFeedPostIdentity(
    val id: String,
    val fingerprint: String,
    val createdAt: Long,
    val updatedAt: Long
)
