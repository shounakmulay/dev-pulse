package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeedPage
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata
import dev.shounakmulay.devpulse.core.data.db.paging.LocalCursorPagingSource
import dev.shounakmulay.devpulse.core.data.db.paging.LocalFeedDiscoveryPagingDataProvider
import dev.shounakmulay.devpulse.core.data.db.query.FeedDiscoveryCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDiscoveryFeedMetadataDao {
    fun getFeedDiscoveryPagingSource(
        database: RoomDatabase,
        query: LocalFeedDiscoveryQuery,
    ): PagingSource<FeedDiscoveryCursor, LocalFeedDiscoveryFeedPage> {
        return LocalCursorPagingSource(
            database = database,
            dataProvider = LocalFeedDiscoveryPagingDataProvider(
                dao = this,
                query = query,
            ),
        )
    }

    @Query(
        """
        SELECT feed.*,
        EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url) AS subscribed,
        (SELECT queue.status FROM LocalRssFeedQueue queue
         WHERE queue.url = feed.url AND queue.actionType = 'IMPORT'
         ORDER BY queue.id DESC LIMIT 1) AS importStatus
        FROM LocalFeedDiscoveryFeedMetadata feed
        WHERE feed.id = :id AND EXISTS(
            SELECT 1 FROM LocalFeedDiscoveryCollectionToFeedMapping membership
            WHERE membership.feedId = feed.id
        )
        """
    )
    fun observeFeed(id: LocalUUID): Flow<LocalFeedDiscoveryFeed?>

    @Query(
        """
        SELECT feed.*,
        EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url) AS subscribed,
        (SELECT queue.status FROM LocalRssFeedQueue queue
         WHERE queue.url = feed.url AND queue.actionType = 'IMPORT'
         ORDER BY queue.id DESC LIMIT 1) AS importStatus,
        mapping.position AS position
        FROM LocalFeedDiscoveryFeedMetadata feed
        JOIN LocalFeedDiscoveryCollectionToFeedMapping mapping ON mapping.feedId = feed.id
        WHERE mapping.collectionId = :collectionId
        AND (
            :afterPosition IS NULL OR mapping.position > :afterPosition
            OR (mapping.position = :afterPosition AND (feed.id > :afterId OR (:includeCursor AND feed.id = :afterId)))
        )
        ORDER BY mapping.position, feed.id
        LIMIT :limit
        """
    )
    suspend fun getCollectionPageAfter(
        collectionId: LocalUUID,
        afterPosition: Int? = null,
        afterId: LocalUUID? = null,
        includeCursor: Boolean = false,
        limit: Int,
    ): List<LocalFeedDiscoveryFeedPage.Collection>

    @Query(
        """
        SELECT feed.*,
        EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url) AS subscribed,
        (SELECT queue.status FROM LocalRssFeedQueue queue
         WHERE queue.url = feed.url AND queue.actionType = 'IMPORT'
         ORDER BY queue.id DESC LIMIT 1) AS importStatus,
        mapping.position AS position
        FROM LocalFeedDiscoveryFeedMetadata feed
        JOIN LocalFeedDiscoveryCollectionToFeedMapping mapping ON mapping.feedId = feed.id
        WHERE mapping.collectionId = :collectionId
        AND (mapping.position < :beforePosition OR (mapping.position = :beforePosition AND feed.id < :beforeId))
        ORDER BY mapping.position DESC, feed.id DESC
        LIMIT :limit
        """
    )
    suspend fun getCollectionPageBefore(
        collectionId: LocalUUID,
        beforePosition: Int,
        beforeId: LocalUUID,
        limit: Int,
    ): List<LocalFeedDiscoveryFeedPage.Collection>

    @Query(
        """
        SELECT feed.id, mapping.position AS `position`
        FROM LocalFeedDiscoveryFeedMetadata feed
        JOIN LocalFeedDiscoveryCollectionToFeedMapping mapping ON mapping.feedId = feed.id
        WHERE mapping.collectionId = :collectionId
        AND feed.id = :id
        """
    )
    suspend fun getCollectionCursor(
        collectionId: LocalUUID,
        id: LocalUUID,
    ): FeedDiscoveryCursor.Collection?

    @Query(
        """
        SELECT feed.*,
        EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url) AS subscribed,
        (SELECT queue.status FROM LocalRssFeedQueue queue
         WHERE queue.url = feed.url AND queue.actionType = 'IMPORT'
         ORDER BY queue.id DESC LIMIT 1) AS importStatus,
        NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 0, '${HL_START}', '${HL_END}'), feed.title) AS search_highlightedTitle,
        CASE WHEN highlight(LocalFeedDiscoveryFeedMetadataFts, 1, '${HL_START}', '${HL_END}') != feed.description
             THEN snippet(LocalFeedDiscoveryFeedMetadataFts, 1, '${HL_START}', '${HL_END}', '...', 30) END AS search_highlightedDescription,
        NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 2, '${HL_START}', '${HL_END}'), feed.url) AS search_highlightedUrl,
        NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 3, '${HL_START}', '${HL_END}'), feed.websiteUrl) AS search_highlightedWebsiteUrl,
        search.rank AS `rank`
        FROM LocalFeedDiscoveryFeedMetadata feed
        JOIN LocalFeedDiscoveryFeedMetadataFts search ON search.rowid = feed.rowid
        WHERE LocalFeedDiscoveryFeedMetadataFts MATCH :query
        AND EXISTS(
            SELECT 1 FROM LocalFeedDiscoveryCollectionToFeedMapping membership
            WHERE membership.feedId = feed.id
              AND (:collectionId IS NULL OR membership.collectionId = :collectionId)
        )
        AND (
            :afterRank IS NULL OR search.rank > :afterRank
            OR (search.rank = :afterRank AND (feed.id > :afterId OR (:includeCursor AND feed.id = :afterId)))
        )
        ORDER BY search.rank, feed.id
        LIMIT :limit
        """
    )
    suspend fun getSearchPageAfter(
        query: String,
        collectionId: LocalUUID?,
        afterRank: Double? = null,
        afterId: LocalUUID? = null,
        includeCursor: Boolean = false,
        limit: Int,
    ): List<LocalFeedDiscoveryFeedPage.Search>

    @Query(
        """
        SELECT feed.*,
        EXISTS(SELECT 1 FROM LocalRssFeed subscribed WHERE subscribed.sourceUrl = feed.url) AS subscribed,
        (SELECT queue.status FROM LocalRssFeedQueue queue
         WHERE queue.url = feed.url AND queue.actionType = 'IMPORT'
         ORDER BY queue.id DESC LIMIT 1) AS importStatus,
        NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 0, '${HL_START}', '${HL_END}'), feed.title) AS search_highlightedTitle,
        CASE WHEN highlight(LocalFeedDiscoveryFeedMetadataFts, 1, '${HL_START}', '${HL_END}') != feed.description
             THEN snippet(LocalFeedDiscoveryFeedMetadataFts, 1, '${HL_START}', '${HL_END}', '...', 30) END AS search_highlightedDescription,
        NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 2, '${HL_START}', '${HL_END}'), feed.url) AS search_highlightedUrl,
        NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 3, '${HL_START}', '${HL_END}'), feed.websiteUrl) AS search_highlightedWebsiteUrl,
        search.rank AS `rank`
        FROM LocalFeedDiscoveryFeedMetadata feed
        JOIN LocalFeedDiscoveryFeedMetadataFts search ON search.rowid = feed.rowid
        WHERE LocalFeedDiscoveryFeedMetadataFts MATCH :query
        AND EXISTS(
            SELECT 1 FROM LocalFeedDiscoveryCollectionToFeedMapping membership
            WHERE membership.feedId = feed.id
              AND (:collectionId IS NULL OR membership.collectionId = :collectionId)
        )
        AND (search.rank < :beforeRank OR (search.rank = :beforeRank AND feed.id < :beforeId))
        ORDER BY search.rank DESC, feed.id DESC
        LIMIT :limit
        """
    )
    suspend fun getSearchPageBefore(
        query: String,
        collectionId: LocalUUID?,
        beforeRank: Double,
        beforeId: LocalUUID,
        limit: Int,
    ): List<LocalFeedDiscoveryFeedPage.Search>

    @Query(
        """
        SELECT feed.id, search.rank AS `rank`
        FROM LocalFeedDiscoveryFeedMetadata feed
        JOIN LocalFeedDiscoveryFeedMetadataFts search ON search.rowid = feed.rowid
        WHERE LocalFeedDiscoveryFeedMetadataFts MATCH :query
        AND EXISTS(
            SELECT 1 FROM LocalFeedDiscoveryCollectionToFeedMapping membership
            WHERE membership.feedId = feed.id
              AND (:collectionId IS NULL OR membership.collectionId = :collectionId)
        )
        AND feed.id = :id
        """
    )
    suspend fun getSearchCursor(
        query: String,
        collectionId: LocalUUID?,
        id: LocalUUID,
    ): FeedDiscoveryCursor.Search?

    @Upsert
    suspend fun upsert(feedMetadata: LocalFeedDiscoveryFeedMetadata)

    @Upsert
    suspend fun upsert(feedMetadata: List<LocalFeedDiscoveryFeedMetadata>)
}
