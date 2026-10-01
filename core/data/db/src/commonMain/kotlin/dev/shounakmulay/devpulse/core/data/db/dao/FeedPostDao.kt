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
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
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
    ): PagingSource<FeedPostCursor, LocalRssPostWithFeedAndSearch> {
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



    @Delete
    suspend fun deletePosts(posts: List<LocalRssContentFeedPost>)

    @Query("SELECT * FROM LocalRssContentFeedPost WHERE id = :id")
    suspend fun getPost(id: LocalUUID): LocalRssContentFeedPost?


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
            f.updatedAt AS feed_updatedAt,
            NULL AS search_highlightedTitle,
            NULL AS search_highlightedDescription,
            NULL AS search_highlightedContent
        FROM LocalRssContentFeedPost p
        INNER JOIN LocalRssFeed f ON p.feedId = f.id
        WHERE p.id = :id
        """
    )
    fun observePost(id: LocalUUID): Flow<LocalRssPostWithFeedAndSearch>

    @Query("UPDATE LocalRssContentFeedPost SET bookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: LocalUUID, isBookmarked: Boolean)

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
            f.updatedAt AS feed_updatedAt,
            NULL AS search_highlightedTitle,
            NULL AS search_highlightedDescription,
            NULL AS search_highlightedContent
        FROM LocalRssContentFeedPost p
        INNER JOIN LocalRssFeed f ON p.feedId = f.id
        ORDER BY publishedAtEpochMillis DESC,  p.id DESC
        LIMIT :limit
        """
    )
    fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedAndSearch>>

    @RawQuery(
        observedEntities = [
            LocalRssContentFeedPost::class,
            LocalRssFeed::class,
            LocalRssPostToTagMapping::class,
            LocalRssPostTag::class
        ]
    )
    suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedAndSearch>

    @RawQuery
    suspend fun getSearchResults(query: RoomRawQuery): List<LocalRssPostWithFeedAndSearch>


    @Query("SELECT publishedAtEpochMillis FROM LocalRssContentFeedPost WHERE feedId = :fromUuid ORDER BY publishedAtEpochMillis DESC LIMIT 1")
    suspend fun getLatestPostPublishedTimeForFeed(fromUuid: LocalUUID): Long?

    @Query(
        """
    SELECT
        post.*,
        feed.id AS feed_id,
        feed.title AS feed_title,
        feed.name AS feed_name,
        feed.pinned AS feed_pinned,
        feed.sourceUrl AS feed_sourceUrl,
        feed.link AS feed_link,
        feed.createdAt AS feed_createdAt,
        feed.updatedAt AS feed_updatedAt,
        feed.lastOpenedAt AS feed_lastOpenedAt,

        highlight(
            LocalRssContentFeedPostFts,
            0,
            :hlStart,
            :hlEnd
        ) AS search_highlightedTitle,

        snippet(
            LocalRssContentFeedPostFts,
            1,
            :hlStart,
            :hlEnd,
            "...",
            :snippetLength
        ) AS search_highlightedDescription,

        snippet(
            LocalRssContentFeedPostFts,
            2,
            :hlStart,
            :hlEnd,
            "...",
            :snippetLength
        ) AS search_highlightedContent

    FROM LocalRssContentFeedPostFts AS postFts
    JOIN LocalRssContentFeedPost AS post
        ON post.rowId = postFts.rowId
    JOIN LocalRssFeed AS feed
        ON feed.id = post.feedId
    WHERE LocalRssContentFeedPostFts MATCH :query

    ORDER BY rank
    LIMIT :limit
    """
    )
    suspend fun searchPosts(
        query: String,
        snippetLength: Int = 30,
        limit: Int = 25,
        hlStart: String = HL_START,
        hlEnd: String = HL_END,
    ): List<LocalRssPostWithFeedAndSearch>
}
