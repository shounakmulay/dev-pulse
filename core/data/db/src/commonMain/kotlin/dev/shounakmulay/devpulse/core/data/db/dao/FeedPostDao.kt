package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.MapColumn
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RoomDatabase
import androidx.room3.RoomRawQuery
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostTag
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostToTagMapping
import dev.shounakmulay.devpulse.core.data.db.paging.LocalCursorPagingSource
import dev.shounakmulay.devpulse.core.data.db.paging.LocalRssPostWithFeedMetadataPagingDataProvider
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedPostDao {

    fun getFeedPostPagingSource(
        database: RoomDatabase,
        query: LocalFeedPostQuery
    ): PagingSource<FeedPostCursor, LocalRssPostWithFeedMetadataProjection> {
        return LocalCursorPagingSource(
            database = database,
            dataProvider = LocalRssPostWithFeedMetadataPagingDataProvider(
                feedPostDao = this,
                query = query
            )
        )
    }

    @Upsert
    suspend fun upsertPost(post: LocalRssContentFeedPost)

    @Upsert
    suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>)

    @Upsert
    suspend fun upsertPostCategories(categories: List<LocalRssPostCategory>)

    @Query("DELETE FROM LocalRssPostCategory WHERE postId = :postId")
    suspend fun deletePostCategories(postId: String)

    @Query("DELETE FROM LocalRssPostCategory WHERE postId IN (:postIds)")
    suspend fun deletePostCategories(postIds: Set<String>)

    @Delete
    suspend fun deletePosts(posts: List<LocalRssContentFeedPost>)

    @Query("SELECT * FROM LocalRssContentFeedPost WHERE id = :id")
    suspend fun getPost(id: String): LocalRssContentFeedPost


    @Query(
        """
        SELECT
            p.*,
            f.id AS feed_id,
            f.title AS feed_title,
            f.name AS feed_name,
            f.pinned AS feed_pinned,
            f.sourceUrl AS feed_sourceUrl,
            f.link AS feed_link,
            f.createdAt AS feed_createdAt,
            f.updatedAt AS feed_updatedAt
        FROM LocalRssContentFeedPost p
        INNER JOIN LocalRssFeed f ON p.feedId = f.id
        WHERE p.id = :id
        """
    )
    fun observePost(id: String): Flow<LocalRssPostWithFeedMetadataProjection>

    @Query("UPDATE LocalRssContentFeedPost SET bookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)

    @Query(
        """
        SELECT id, fingerprint, bookmarked, createdAt, updatedAt
        FROM LocalRssContentFeedPost
        WHERE fingerprint IN (:fingerprints)
        """
    )
    suspend fun getByFingerprints(
        fingerprints: Set<String>
    ): Map<@MapColumn("fingerprint") String, LocalRssContentFeedPostIdentitySlice>

    @Query(
        """
        SELECT 
            p.*,
            f.id AS feed_id,
            f.title AS feed_title,
            f.name AS feed_name,
            f.pinned AS feed_pinned,
            f.sourceUrl AS feed_sourceUrl,
            f.link AS feed_link,
            f.createdAt AS feed_createdAt,
            f.updatedAt AS feed_updatedAt
        FROM LocalRssContentFeedPost p
        INNER JOIN LocalRssFeed f ON p.feedId = f.id
        ORDER BY publishedAtEpochMillis DESC,  p.id DESC
        LIMIT :limit
        """
    )
    fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedMetadataProjection>>

    @RawQuery(
        observedEntities = [
            LocalRssContentFeedPost::class,
            LocalRssFeed::class,
            LocalRssPostToTagMapping::class,
            LocalRssPostTag::class
        ]
    )
    suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedMetadataProjection>
}
