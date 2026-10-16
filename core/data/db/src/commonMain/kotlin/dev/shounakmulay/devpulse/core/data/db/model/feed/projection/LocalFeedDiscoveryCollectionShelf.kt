package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection

data class LocalFeedDiscoveryCollectionShelf(
    @Embedded(prefix = "collection_") val collection: LocalFeedDiscoveryCollection,
    @Embedded(prefix = "feed_") val feed: LocalFeedDiscoveryFeed?,
)
