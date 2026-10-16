package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RoomDatabase
import androidx.room3.RoomRawQuery
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata
import dev.shounakmulay.devpulse.core.data.db.paging.LocalCursorPagingSource
import dev.shounakmulay.devpulse.core.data.db.paging.LocalFeedDiscoveryPagingDataProvider
import dev.shounakmulay.devpulse.core.data.db.query.FeedDiscoveryCursor
import dev.shounakmulay.devpulse.core.data.db.query.FeedDiscoverySql
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDiscoveryFeedMetadataDao {
    fun getFeedDiscoveryPagingSource(
        database: RoomDatabase,
        query: LocalFeedDiscoveryQuery,
    ): PagingSource<FeedDiscoveryCursor, LocalFeedDiscoveryFeed> {
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
        SELECT ${FeedDiscoverySql.PROJECTION}
        FROM LocalFeedDiscoveryFeedMetadata feed
        WHERE feed.id = :id AND ${FeedDiscoverySql.MEMBERSHIP}
        """
    )
    fun observeFeed(id: LocalUUID): Flow<LocalFeedDiscoveryFeed?>

    @RawQuery
    suspend fun getPage(query: RoomRawQuery): List<LocalFeedDiscoveryFeed>

    @Upsert
    suspend fun upsert(feedMetadata: LocalFeedDiscoveryFeedMetadata)

    @Upsert
    suspend fun upsert(feedMetadata: List<LocalFeedDiscoveryFeedMetadata>)
}
