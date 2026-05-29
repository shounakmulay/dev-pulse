package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.MapColumn
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedContentDao {

    @Upsert
    suspend fun upsertPost(post: LocalRssContentFeedPost)

    @Upsert
    suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>)

    @Delete
    suspend fun deletePosts(posts: List<LocalRssContentFeedPost>)

    @Query("SELECT * FROM LocalRssContentFeedPost WHERE id = :id")
    suspend fun getPost(id: String): LocalRssContentFeedPost

    @Query(
        """
        SELECT id, fingerprint, createdAt, updatedAt
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
        ORDER BY pubDate DESC, updatedAt DESC
        LIMIT :limit
        """
    )
    fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedMetadataProjection>>

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
