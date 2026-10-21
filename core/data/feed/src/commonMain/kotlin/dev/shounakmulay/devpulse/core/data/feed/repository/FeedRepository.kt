package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun upsertFeed(feed: RssFeed)
    fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>>
    fun searchFeeds(query: String, snippetLength: Int): Flow<PagingData<RssFeedSearchResult>>
    fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeed>>
    fun getFeed(id: UUID): Flow<RssFeed>
    fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>>
    suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData>
    suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData>
    suspend fun fetchRssFeed(entry: RssFeedQueueEntry): ParsedFeed
    suspend fun deleteFeed(id: UUID)
    suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit>
    suspend fun getFeedIdentityBySourceUrl(url: String): RssFeedIdentity?
}
