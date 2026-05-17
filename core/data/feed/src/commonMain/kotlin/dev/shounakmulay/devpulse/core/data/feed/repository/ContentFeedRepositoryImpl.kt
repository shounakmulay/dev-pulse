package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.RssFeedParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class ContentFeedRepositoryImpl(
    private val rssParser: RssFeedParser,
    private val feedDao: FeedDao,
    private val rssFeedMapper: RssFeedMapper,
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

    override suspend fun parseRssFeed(url: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFeed(id: Int) {
        feedDao.deleteFeeds(listOf(id))
    }
}