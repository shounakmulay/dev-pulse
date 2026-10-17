package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollectionToFeedMapping

@Dao
interface FeedDiscoveryCollectionToFeedMappingDao {
    @Upsert
    suspend fun upsert(mapping: LocalFeedDiscoveryCollectionToFeedMapping)

    @Upsert
    suspend fun upsert(mappings: List<LocalFeedDiscoveryCollectionToFeedMapping>)

    @Query("DELETE FROM LocalFeedDiscoveryCollectionToFeedMapping WHERE collectionId IN (:collectionIds)")
    suspend fun delete(collectionIds: List<String>)
}