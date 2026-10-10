package dev.shounakmulay.devpulse.core.domain.models.feed

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedSearchResult(
    val id: UUID,
    val title: String,
    val name: String,
    val image: RssFeedImage?,
    val link: String?,
    val sourceUrl: String,
    val pinned: Boolean,
    val highlightedTitle: String?,
    val highlightedName: String?,
    val highlightedDescription: String?,
)