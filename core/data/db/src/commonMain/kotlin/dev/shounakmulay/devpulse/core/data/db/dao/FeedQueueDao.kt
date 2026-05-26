package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueue
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueueStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedQueueDao {

    @Query(
        """
        SELECT * FROM LocalRssFeedQueue 
        WHERE status IN ('PROCESSING', 'QUEUED')
        ORDER BY 
            CASE status 
                WHEN 'PROCESSING' THEN 1 
                WHEN 'QUEUED' THEN 2 
                ELSE 3 
            END ASC, 
            CASE requestor
                WHEN 'USER' THEN 1
                WHEN 'APP' THEN 2
                ELSE 3
            END ASC,
            CASE actionType
                WHEN 'IMPORT' THEN 1
                WHEN 'SYNC' THEN 2
                ELSE 3
            END ASC,
            id ASC 
        LIMIT 1
        """
    )
    suspend fun getNext(): LocalRssFeedQueue?

    @Insert
    suspend fun add(feeds: List<LocalRssFeedQueue>)

    @Upsert
    suspend fun upsert(feedQueue: LocalRssFeedQueue)

    @Delete
    suspend fun delete(feedQueue: LocalRssFeedQueue)

    @Query("UPDATE LocalRssFeedQueue SET status = :localStatus WHERE id = :id")
    suspend fun updateStatus(id: Int, localStatus: LocalRssFeedQueueStatus)

    @Query(
        """
        SELECT * FROM LocalRssFeedQueue
        WHERE id IN (
            SELECT MAX(id)
            FROM LocalRssFeedQueue
            WHERE url IN (:urls)
            GROUP BY url
        )
        ORDER BY id DESC
        """
    )
    fun observeEntriesForUrls(urls: List<String>): Flow<List<LocalRssFeedQueue>>
}
