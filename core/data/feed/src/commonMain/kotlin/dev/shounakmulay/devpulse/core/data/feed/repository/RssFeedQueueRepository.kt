package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import kotlinx.coroutines.flow.Flow

interface RssFeedQueueRepository {
    suspend fun enqueue(entry: RssFeedQueueEntry)

    suspend fun enqueue(entries: List<RssFeedQueueEntry>)
    suspend fun updateQueueEntry(entry: RssFeedQueueEntry)

    suspend fun getNextToProcess(): RssFeedQueueEntry?

    fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>>

    suspend fun removeStaleEntries(entry: RssFeedQueueEntry)
}
