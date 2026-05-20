package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.RssFeedParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
internal class ContentFeedRepositoryImpl(
    private val rssParser: RssFeedParser,
    private val feedDao: FeedDao,
    private val rssFeedMapper: RssFeedMapper,
    private val rssContentFeedProcessor: RssContentFeedProcessor
) : ContentFeedRepository {
    override fun getFeedFlow(): Flow<PagingData<RssFeed>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                feedDao.getFeedPagingSource()
            })
            .flow
            .map { pagingData ->
                pagingData.map { localRssFeed ->
                    rssFeedMapper.toRssFeed(localRssFeed)
                }
            }
    }

    override suspend fun addRssFeed(entry: RssFeedQueueEntry) {
        val rssChannel = rssParser.parseFeed(entry.url)
        rssContentFeedProcessor.process(entry = entry, rssChannel = rssChannel)
    }

    override suspend fun deleteFeed(id: String) {
        feedDao.deleteFeeds(listOf(id))
    }
}
