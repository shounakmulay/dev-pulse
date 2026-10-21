package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedSyncMetadataWithIdentity
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedSyncMetadata

@Dao
interface FeedSyncMetadataDao {

    @Query("SELECT * FROM LocalRssFeedSyncMetadata WHERE feedId = :id")
    suspend fun getSyncMetadata(id: LocalUUID): LocalRssFeedSyncMetadata?

    @Query("SELECT * FROM LocalRssFeedSyncMetadata WHERE feedId IN (:ids)")
    suspend fun getSyncMetadata(ids: List<LocalUUID>): List<LocalRssFeedSyncMetadata>

    @Upsert
    suspend fun upsertSyncMetadata(metadata: List<LocalRssFeedSyncMetadata>)

    @Query(
        """
        SELECT
            metadata.*,
            identity.id AS identity_id,
            identity.title AS identity_title,
            identity.name AS identity_name,
            identity.pinned AS identity_pinned,
            identity.sourceUrl AS identity_sourceUrl,
            identity.link AS identity_link,
            identity.createdAt AS identity_createdAt,
            identity.updatedAt AS identity_updatedAt
        FROM LocalRssFeedSyncMetadata AS metadata
        INNER JOIN LocalRssFeed AS identity
        ON metadata.feedId = identity.id
        WHERE metadata.nextEligibleFetchAt <= :currentEpochMillis
        """
    )
    suspend fun getFeedsPastNextEligibleFetch(currentEpochMillis: Long): List<LocalRssFeedSyncMetadataWithIdentity>
}