package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost

@Dao
interface FeedContentDao {

    @Query("SELECT * FROM LocalRssContentFeedPost WHERE id = :id")
    suspend fun getPost(id: String): LocalRssContentFeedPost

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
