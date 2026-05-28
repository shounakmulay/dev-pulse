package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import kotlinx.coroutines.flow.Flow

interface ContentFeedRepository {
    fun getFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>>

    fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeed>>
    fun getRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentity>>
    suspend fun addRssFeed(entry: RssFeedQueueEntry)
    suspend fun deleteFeed(id: String)
    suspend fun setFeedPinned(id: String, pinned: Boolean): Result<Unit>
    fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>>
}
