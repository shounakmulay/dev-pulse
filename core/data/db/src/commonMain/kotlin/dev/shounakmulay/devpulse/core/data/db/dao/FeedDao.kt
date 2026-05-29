package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query("SELECT * FROM LocalRssFeed ORDER BY name, title, updatedAt DESC")
    fun getFeedPagingSource(): PagingSource<Int, LocalRssFeed>

    @Query("SELECT * FROM LocalRssFeed WHERE pinned = '1' ORDER BY name, title, updatedAt DESC")
    fun getPinnedFeedPagingSource(): PagingSource<Int, LocalRssFeed>

    @Query("SELECT * FROM LocalRssFeed ORDER BY pinned DESC, updatedAt DESC LIMIT :count")
     fun getPinnedAndRecentFeeds(count: Int) : Flow<List<LocalRssFeed>>

    @Query("SELECT * FROM LocalRssFeed WHERE id = :id")
    suspend fun getFeed(id: String): LocalRssFeed

    @Query("SELECT * FROM LocalRssFeed WHERE sourceUrl = :sourceUrl")
    suspend fun getFeedBySourceUrl(sourceUrl: String): LocalRssFeed?

    @Query("UPDATE LocalRssFeed SET pinned = :pinned WHERE id = :id")
    suspend fun setFeedPinned(id: String, pinned: Boolean)

    @Query(
        """
        SELECT id, title, pinned, sourceUrl, link, createdAt, updatedAt
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
    suspend fun deleteFeeds(feeds: List<String>)
}
