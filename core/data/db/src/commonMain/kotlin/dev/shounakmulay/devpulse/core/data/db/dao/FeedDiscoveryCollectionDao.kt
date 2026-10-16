package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedDiscoveryCollectionHashSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedDiscoveryCollectionPositionSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection

@Dao
interface FeedDiscoveryCollectionDao {

    @Upsert
    suspend fun upsert(collection: LocalFeedDiscoveryCollection)

    @Upsert
    suspend fun upsert(collections: List<LocalFeedDiscoveryCollection>)

    @Update(entity = LocalFeedDiscoveryCollection::class)
    suspend fun updatePosition(positions: List<LocalFeedDiscoveryCollectionPositionSlice>)

    @Query("SELECT id, path, sha256Hash FROM LocalFeedDiscoveryCollection")
    suspend fun getHashes(): List<LocalFeedDiscoveryCollectionHashSlice>

    @Query("DELETE FROM LocalFeedDiscoveryCollection WHERE id IN (:ids)")
    suspend fun delete(ids: List<LocalUUID>)
}
