package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed

@Dao
interface FeedDao {

    @Query("SELECT * FROM LocalRssFeed ORDER BY title")
    fun getFeedPagingSource(): PagingSource<Int, LocalRssFeed>

    @Query("SELECT * FROM LocalRssFeed WHERE id = :id")
    suspend fun getFeed(id: Int): LocalRssFeed

    @Upsert
    suspend fun upsertFeed(feed: LocalRssFeed)

    @Upsert
    suspend fun upsertFeeds(feeds: List<LocalRssFeed>)

    @Query("DELETE from LocalRssFeed WHERE id IN (:feeds)")
    suspend fun deleteFeeds(feeds: List<Int>)
}
