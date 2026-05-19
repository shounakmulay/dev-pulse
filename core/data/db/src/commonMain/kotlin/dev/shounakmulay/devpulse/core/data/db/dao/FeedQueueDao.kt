package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueue

@Dao
interface FeedQueueDao {

    @Query(
        """
        SELECT * FROM LocalRssFeedQueue 
        ORDER BY 
            CASE status 
                WHEN 'PROCESSING' THEN 1 
                WHEN 'QUEUED' THEN 2 
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

}