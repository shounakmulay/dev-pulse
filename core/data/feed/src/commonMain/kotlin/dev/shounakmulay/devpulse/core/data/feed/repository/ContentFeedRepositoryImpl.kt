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
import dev.shounakmulay.devpulse.core.logging.DPLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory(binds = [ContentFeedRepository::class])
internal class ContentFeedRepositoryImpl(
    private val rssParser: RssFeedParser,
    private val feedDao: FeedDao,
    private val rssFeedMapper: RssFeedMapper,
    private val rssContentFeedProcessor: RssContentFeedProcessor,
    logger: DPLogger
) : ContentFeedRepository {
    private val logger = logger.withTag(Tag)

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
        logger.d { "RSS parse started queueId=${entry.id} source=${entry.url.sourceSummary()}" }
        val rssChannel = try {
            rssParser.parseFeed(entry.url)
        } catch (e: Exception) {
            logger.e(e) {
                "RSS parse failed queueId=${entry.id} source=${entry.url.sourceSummary()}"
            }
            throw e
        }
        logger.d {
            "RSS parse succeeded queueId=${entry.id} source=${entry.url.sourceSummary()} itemCount=${rssChannel.items.size}"
        }
        rssContentFeedProcessor.process(entry = entry, rssChannel = rssChannel)
    }

    override suspend fun deleteFeed(id: String) {
        feedDao.deleteFeeds(listOf(id))
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host = withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "ContentFeedRepository"
    }
}
