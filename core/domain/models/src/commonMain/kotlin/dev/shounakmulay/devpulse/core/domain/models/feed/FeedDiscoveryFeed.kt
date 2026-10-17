package dev.shounakmulay.devpulse.core.domain.models.feed

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class FeedDiscoveryFeed(
    val id: UUID,
    val title: String,
    val description: String?,
    val url: String,
    val websiteUrl: String?,
    val iconUrl: String?,
    val importState: FeedDiscoveryImportState,
    val search: FeedDiscoverySearchHighlights? = null,
)

data class FeedDiscoverySearchHighlights(
    val highlightedTitle: String?,
    val highlightedDescription: String?,
    val highlightedUrl: String?,
    val highlightedWebsiteUrl: String?,
)

enum class FeedDiscoveryImportState { Added, Adding, Queued, Failed, NotAdded }
