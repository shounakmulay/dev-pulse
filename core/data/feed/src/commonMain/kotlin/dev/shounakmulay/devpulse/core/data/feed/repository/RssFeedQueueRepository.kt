package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import kotlinx.coroutines.flow.Flow

interface RssFeedQueueRepository {
    suspend fun enqueue(entries: List<RssFeedQueueEntry>)
    suspend fun updateQueueStatus(id: Int, status: RssFeedQueueStatus)

    suspend fun getNextToProcess(): RssFeedQueueEntry?

    fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>>
}
