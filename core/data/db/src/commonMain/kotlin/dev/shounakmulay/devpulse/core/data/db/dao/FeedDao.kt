package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedSearchResult
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedWithUserState
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query("SELECT f.*, COALESCE(fs.pinned, 0) AS state_pinned, fs.lastOpenedAt AS state_lastOpenedAt FROM LocalRssFeed f LEFT JOIN LocalRssFeedUserState fs ON fs.feedId = f.id ORDER BY f.name, f.title, f.updatedAt DESC, f.id")
    fun getFeedPagingSource(): PagingSource<Int, LocalRssFeedWithUserState>

    @Query("SELECT f.*, COALESCE(fs.pinned, 0) AS state_pinned, fs.lastOpenedAt AS state_lastOpenedAt FROM LocalRssFeed f LEFT JOIN LocalRssFeedUserState fs ON fs.feedId = f.id WHERE fs.pinned = 1 ORDER BY f.name, f.title, f.updatedAt DESC, f.id")
    fun getPinnedFeedPagingSource(): PagingSource<Int, LocalRssFeedWithUserState>

    @Query("SELECT f.*, COALESCE(fs.pinned, 0) AS state_pinned, fs.lastOpenedAt AS state_lastOpenedAt FROM LocalRssFeed f LEFT JOIN LocalRssFeedUserState fs ON fs.feedId = f.id ORDER BY COALESCE(fs.pinned, 0) DESC, f.name, f.title, f.id LIMIT :count")
    fun getPinnedAndRecentFeeds(count: Int): Flow<List<LocalRssFeedWithUserState>>

    @Query("SELECT * FROM LocalRssFeed WHERE id = :id")
    suspend fun getFeed(id: String): LocalRssFeed

    @Query("SELECT f.*, COALESCE(fs.pinned, 0) AS state_pinned, fs.lastOpenedAt AS state_lastOpenedAt FROM LocalRssFeed f LEFT JOIN LocalRssFeedUserState fs ON fs.feedId = f.id WHERE f.id = :id")
    fun observeFeed(id: LocalUUID): Flow<LocalRssFeedWithUserState>

    @Query("SELECT * FROM LocalRssFeed WHERE sourceUrl = :sourceUrl")
    suspend fun getFeedBySourceUrl(sourceUrl: String): LocalRssFeed?


    @Query(
        """
        SELECT id, title, name, sourceUrl, link, createdAt, updatedAt
        FROM LocalRssFeed
        WHERE sourceUrl = :sourceUrl
    """
    )
    suspend fun getFeedIdentityBySourceUrl(sourceUrl: String): LocalRssFeedIdentitySlice?

    @Upsert
    suspend fun upsertFeed(feed: LocalRssFeed)

    @Upsert
    suspend fun upsertFeeds(feeds: List<LocalRssFeed>)

    @Query("DELETE from LocalRssFeed WHERE id IN (:feeds)")
    suspend fun deleteFeeds(feeds: List<LocalUUID>)

    @Query(
        """
        SELECT
            feed.id,
            feed.title,
            feed.name,
            COALESCE(fs.pinned, 0) AS pinned,
            feed.link,
            feed.sourceUrl,
            feed.image_title,
            feed.image_url,
            feed.image_description,
            feed.image_link,
            highlight(LocalRssFeedFts, 0, :hlStart, :hlEnd) AS highlightedTitle,
            highlight(LocalRssFeedFts, 1, :hlStart, :hlEnd) AS highlightedName,
            snippet(LocalRssFeedFts, 2, :hlStart, :hlEnd, "...", :snippetLength) AS highlightedDescription
        FROM LocalRssFeedFts AS feedFts
        JOIN LocalRssFeed AS feed ON feed.rowId = feedFts.rowId
        LEFT JOIN LocalRssFeedUserState fs ON fs.feedId = feed.id
        WHERE LocalRssFeedFts MATCH :query
        ORDER BY rank, feed.id
    """
    )
    fun searchFeeds(
        query: String,
        snippetLength: Int = 30,
        hlStart: String = HL_START,
        hlEnd: String = HL_END
    ): PagingSource<Int, LocalRssFeedSearchResult>
}
