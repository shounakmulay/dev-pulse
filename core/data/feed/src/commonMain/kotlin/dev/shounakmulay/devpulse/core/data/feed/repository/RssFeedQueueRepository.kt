package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus

interface RssFeedQueueRepository {
    suspend fun enqueue(entry: RssFeedQueueEntry)
    suspend fun updateQueueStatus(id: Int, status: RssFeedQueueStatus)

    suspend fun getNextToProcess(): RssFeedQueueEntry?
}
