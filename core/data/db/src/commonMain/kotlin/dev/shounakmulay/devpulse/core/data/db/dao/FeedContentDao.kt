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
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssPostTag
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssPostToTagMapping
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.paging.LocalCursorPagingSource
import dev.shounakmulay.devpulse.core.data.db.paging.LocalRssPostWithFeedMetadataPagingDataProvider
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedContentDao {

    fun getFeedPostPagingSource(
        database: RoomDatabase,
        query: LocalFeedPostQuery
    ): PagingSource<FeedPostCursor, LocalRssPostWithFeedMetadataProjection> {
        return LocalCursorPagingSource(
            database = database,
            dataProvider = LocalRssPostWithFeedMetadataPagingDataProvider(
                feedContentDao = this,
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

    @Query("SELECT * FROM LocalRssContentFeedPost WHERE feedId = :feedId ORDER BY id DESC")
    suspend fun getPostsForFeed(feedId: String): List<LocalRssContentFeedPost>

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

    @Query(
        """
        SELECT * FROM LocalRssContentFeedPost
        ORDER BY id DESC
        LIMIT :limit
        """
    )
    suspend fun getInitialPage(limit: Int): List<LocalRssContentFeedPost>

    @Query(
        """
        SELECT * FROM LocalRssContentFeedPost
        WHERE  id < :id
        ORDER BY id DESC
        LIMIT :limit
        """
    )
    suspend fun getPageAfter(
        id: String,
        limit: Int
    ): List<LocalRssContentFeedPost>

    @Query(
        """
        SELECT * FROM LocalRssContentFeedPost
        WHERE id > :id
        ORDER BY id ASC
        LIMIT :limit
        """
    )
    suspend fun getPageBeforeQuery(
        id: String,
        limit: Int
    ): List<LocalRssContentFeedPost>

    @Query(
        """
        SELECT * FROM LocalRssContentFeedPost
        WHERE id <= :id
        ORDER BY id DESC
        LIMIT :limit
        """
    )
    suspend fun getRefreshPageAround(
        id: String,
        limit: Int
    ): List<LocalRssContentFeedPost>
}
