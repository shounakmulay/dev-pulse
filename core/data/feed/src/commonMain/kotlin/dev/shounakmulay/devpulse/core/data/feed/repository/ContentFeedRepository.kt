package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import kotlinx.coroutines.flow.Flow

interface ContentFeedRepository {
    fun getFeedFlow(): Flow<PagingData<RssFeed>>
    suspend fun addRssFeed(entry: RssFeedQueueEntry)
    suspend fun deleteFeed(id: String)
}
