package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueStatus
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata

data class LocalFeedDiscoveryFeed(
    @Embedded val metadata: LocalFeedDiscoveryFeedMetadata,
    val subscribed: Boolean,
    val importStatus: LocalRssFeedQueueStatus?,
)
