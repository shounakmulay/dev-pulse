package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedDiscoveryCollectionHashSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedDiscoveryCollectionPositionSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDiscoveryCollectionDao {

    @Query("SELECT * FROM LocalFeedDiscoveryCollection WHERE id = :id")
    fun observeCollection(id: LocalUUID): Flow<LocalFeedDiscoveryCollection?>

    @Query(
        """
        SELECT
            collection.id AS collection_id,
            collection.catalogId AS collection_catalogId,
            collection.path AS collection_path,
            collection.feedCount AS collection_feedCount,
            collection.sha256Hash AS collection_sha256Hash,
            collection.slug AS collection_slug,
            collection.title AS collection_title,
            collection.position AS collection_position,
            collection.description AS collection_description,
            feed.id AS feed_id,
            feed.title AS feed_title,
            feed.description AS feed_description,
            feed.feedFormat AS feed_feedFormat,
            feed.url AS feed_url,
            feed.iconUrl AS feed_iconUrl,
            feed.languageTag AS feed_languageTag,
            feed.websiteUrl AS feed_websiteUrl,
            CASE WHEN feed.id IS NOT NULL THEN EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url) END AS feed_subscribed,
            (SELECT queue.status FROM LocalRssFeedQueue queue WHERE queue.url = feed.url AND queue.actionType = 'IMPORT' ORDER BY queue.id DESC LIMIT 1) AS feed_importStatus,
            NULL AS feed_search_highlightedTitle,
            NULL AS feed_search_highlightedDescription,
            NULL AS feed_search_highlightedUrl,
            NULL AS feed_search_highlightedWebsiteUrl,
            mapping.position AS feed_position,
            CASE WHEN feed.id IS NOT NULL THEN 0.0 END AS feed_rank
        FROM LocalFeedDiscoveryCollection collection
        LEFT JOIN LocalFeedDiscoveryCollectionToFeedMapping mapping
            ON mapping.collectionId = collection.id AND mapping.position < :previewCount
        LEFT JOIN LocalFeedDiscoveryFeedMetadata feed ON feed.id = mapping.feedId
        ORDER BY collection.position, collection.id, mapping.position, feed.id
        """
    )
    fun observeCollectionShelves(previewCount: Int): Flow<List<LocalFeedDiscoveryCollectionShelf>>

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
