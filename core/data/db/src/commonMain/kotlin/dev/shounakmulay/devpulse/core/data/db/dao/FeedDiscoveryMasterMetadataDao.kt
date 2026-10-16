package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryMasterMetadata

@Dao
interface FeedDiscoveryMasterMetadataDao {

    @Query("SELECT EXISTS(SELECT 1 FROM LocalFeedDiscoveryMasterMetadata)")
    suspend fun hasMasterMetadata(): Boolean

    @Query("SELECT etag FROM LocalFeedDiscoveryMasterMetadata")
    suspend fun getEtag(): String?

    @Upsert
    suspend fun upsert(masterMetadata: LocalFeedDiscoveryMasterMetadata)
}