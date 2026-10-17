package dev.shounakmulay.devpulse.core.domain.models.feed

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class FeedDiscoveryCollection(
    val id: UUID,
    val title: String,
    val description: String,
    val feedCount: Int,
)

data class FeedDiscoveryCollectionShelf(
    val collection: FeedDiscoveryCollection,
    val feeds: List<FeedDiscoveryFeed>,
)
