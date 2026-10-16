package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata

@Dao
interface FeedDiscoveryFeedMetadataDao {
    @Upsert
    suspend fun upsert(feedMetadata: LocalFeedDiscoveryFeedMetadata)

    @Upsert
    suspend fun upsert(feedMetadata: List<LocalFeedDiscoveryFeedMetadata>)
}