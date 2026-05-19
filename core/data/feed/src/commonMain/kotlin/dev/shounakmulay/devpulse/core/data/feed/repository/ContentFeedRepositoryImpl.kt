package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.prof18.rssparser.model.RssChannel
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.RssFeedParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    override suspend fun addRssFeed(url: String) = coroutineScope {
        val rssChannel = rssParser.parseFeed(url)

        val localRssFeed = async { buildLocalRssFeed(rssChannel) }
        val localRssContentFeedPosts = async { buildLocalRssContentFeedPost(rssChannel, localRssFeed)}
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun buildLocalRssContentFeedPost(
        rssChannel: RssChannel,
        localRssFeed: Deferred<LocalRssFeed>
    ) {
//       rssChannel.items.asFlow()
//           .chunked(50)
//           .map {
//              it.map {
//                  LocalRssContentFeedPost()
//              }
//           }.onEach {
//               // save to local
//           }
//           .collect()
    }

    override suspend fun deleteFeed(id: String) {
        feedDao.deleteFeeds(listOf(id))
    }

    private suspend fun buildLocalRssFeed(rssChannel: RssChannel): LocalRssFeed {
//        LocalRssFeed()
        TODO()
    }
}
