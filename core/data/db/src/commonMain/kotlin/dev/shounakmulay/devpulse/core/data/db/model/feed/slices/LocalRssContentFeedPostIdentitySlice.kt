package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

data class LocalRssContentFeedPostIdentitySlice(
    val id: LocalUUID,
    val fingerprint: String,
    val bookmarked: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
