package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

data class LocalRssPostSearchResult(
    val id: LocalUUID,
    val feedId: LocalUUID,
    val bookmarked: Boolean,
    val link: String?,
    val publishedAtEpochMillis: Long?,
    val updatedAt: Long,
    val highlightedTitle: String,
    val highlightedDescription: String?,
    val highlightedContent: String?,
)