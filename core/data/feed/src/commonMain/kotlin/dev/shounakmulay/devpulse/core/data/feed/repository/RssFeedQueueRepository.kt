package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import kotlinx.coroutines.flow.Flow

interface RssFeedQueueRepository {
    suspend fun enqueue(entry: RssFeedQueueEntry)

    suspend fun enqueue(entries: List<RssFeedQueueEntry>)
    suspend fun updateQueueEntry(entry: RssFeedQueueEntry)

    suspend fun getNextToProcess(): RssFeedQueueEntry?

    fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>>

    suspend fun removeStaleEntries(entry: RssFeedQueueEntry)
    fun observeQueuePagingData(status: Set<RssFeedQueueStatus>): Flow<PagingData<RssFeedQueueEntry>>

    fun observeQueue(status: Set<RssFeedQueueStatus>): Flow<List<RssFeedQueueEntry>>
}
