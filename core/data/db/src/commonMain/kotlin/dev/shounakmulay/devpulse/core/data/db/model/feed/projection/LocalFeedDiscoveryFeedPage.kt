package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded

sealed interface LocalFeedDiscoveryFeedPage {
    val feed: LocalFeedDiscoveryFeed

    data class Collection(
        @Embedded override val feed: LocalFeedDiscoveryFeed,
        val position: Int,
    ) : LocalFeedDiscoveryFeedPage

    data class Search(
        @Embedded override val feed: LocalFeedDiscoveryFeed,
        @Embedded(prefix = "search_") val search: LocalFeedDiscoverySearchHighlights?,
        val rank: Double,
    ) : LocalFeedDiscoveryFeedPage
}

data class LocalFeedDiscoverySearchHighlights(
    val highlightedTitle: String?,
    val highlightedDescription: String?,
    val highlightedUrl: String?,
    val highlightedWebsiteUrl: String?,
)
