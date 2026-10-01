package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

data class LocalRssPostContentSearchResult(
    val id: LocalUUID,
    val feedId: LocalUUID,
    val link: String?,
    val publishedAtEpochMillis: Long?,
    val updatedAt: Long,
    val highlightedContent: String,
)