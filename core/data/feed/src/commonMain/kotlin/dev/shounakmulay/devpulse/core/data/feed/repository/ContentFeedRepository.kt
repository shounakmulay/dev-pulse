package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import kotlinx.coroutines.flow.Flow

interface ContentFeedRepository {
    fun getFeedFlow(): Flow<PagingData<RssFeed>>
    suspend fun parseRssFeed(url: String)
    suspend fun deleteFeed(id: Int)
}