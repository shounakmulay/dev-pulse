package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedWithSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query(
        """
        SELECT *, NULL AS highlightedTitle, NULL AS highlightedName, NULL AS highlightedDescription
        FROM LocalRssFeed
        WHERE NOT :pinnedOnly OR pinned = 1
        ORDER BY name, title, updatedAt DESC, id
        """
    )
    fun getFeedPagingSource(pinnedOnly: Boolean): PagingSource<Int, LocalRssFeedWithSearch>

    @Query("SELECT * FROM LocalRssFeed ORDER BY pinned DESC, updatedAt DESC LIMIT :count")
    fun getPinnedAndRecentFeeds(count: Int): Flow<List<LocalRssFeed>>

    @Query("SELECT * FROM LocalRssFeed WHERE id = :id")
    suspend fun getFeed(id: String): LocalRssFeed

    @Query("SELECT * FROM LocalRssFeed WHERE id = :id")
    fun observeFeed(id: LocalUUID): Flow<LocalRssFeed>

    @Query("SELECT * FROM LocalRssFeed WHERE sourceUrl = :sourceUrl")
    suspend fun getFeedBySourceUrl(sourceUrl: String): LocalRssFeed?

    @Query("UPDATE LocalRssFeed SET pinned = :pinned WHERE id = :id")
    suspend fun setFeedPinned(id: LocalUUID, pinned: Boolean)

    @Query(
        """
        SELECT id, title, name, pinned, sourceUrl, link, createdAt, updatedAt, lastOpenedAt
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
            feed.*,
            highlight(LocalRssFeedFts, 0, :hlStart, :hlEnd) AS highlightedTitle,
            highlight(LocalRssFeedFts, 1, :hlStart, :hlEnd) AS highlightedName,
            snippet(LocalRssFeedFts, 2, :hlStart, :hlEnd, "...", :snippetLength) AS highlightedDescription
        FROM LocalRssFeedFts AS feedFts
        JOIN LocalRssFeed AS feed ON feed.rowId = feedFts.rowId
        WHERE LocalRssFeedFts MATCH :query AND (NOT :pinnedOnly OR feed.pinned = 1)
        ORDER BY rank, feed.id
    """
    )
    fun searchFeeds(
        query: String,
        pinnedOnly: Boolean,
        snippetLength: Int = 30,
        hlStart: String = HL_START,
        hlEnd: String = HL_END
    ): PagingSource<Int, LocalRssFeedWithSearch>
}
