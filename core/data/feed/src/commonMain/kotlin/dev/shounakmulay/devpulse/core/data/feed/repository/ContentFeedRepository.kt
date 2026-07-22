package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import kotlinx.coroutines.flow.Flow

interface ContentFeedRepository {
    fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>>

    fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeed>>
    fun getRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentity>>
    fun getFeedPostsFlow(query: RssPostQuery, pagingConfig: PagingConfig): Flow<PagingData<RssPostWithFeedIdentity>>
    fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>>
    fun getFeed(id: String): Flow<RssFeed>
    suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData>
    suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData>
    suspend fun addRssFeed(entry: RssFeedQueueEntry)
    suspend fun deleteFeed(id: String)
    suspend fun setFeedPinned(id: String, pinned: Boolean): Result<Unit>
    suspend fun setPostBookmarked(id: String, bookmarked: Boolean): Result<Unit>
}
