package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.paging.FeedPostPagingSourceProvider
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostQueryMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.OpmlParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.model.ParsedOpmlDocument
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory(binds = [FeedRepository::class])
internal class FeedRepositoryImpl(
    private val feedImportFallbackParser: FeedImportFallbackParser,
    private val opmlParser: OpmlParser,
    private val networkClient: DevPulseNetworkClient,
    private val feedDao: FeedDao,
    private val feedPostDao: FeedPostDao,
    private val feedPostPagingSourceProvider: FeedPostPagingSourceProvider,
    private val rssFeedMapper: RssFeedMapper,
    private val rssPostMapper: RssPostMapper,
    private val rssPostQueryMapper: RssPostQueryMapper,
    private val uuidMapper: UuidMapper,
    logger: DPLogger
) : FeedRepository {
    private val logger = logger.withTag(Tag)

    override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>> {
        return Pager(
            config = pagingConfig,
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

    override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                feedDao.getPinnedFeedPagingSource()
            })
            .flow
            .map { pagingData ->
                pagingData.map { localRssFeed ->
                    rssFeedMapper.toRssFeed(localRssFeed)
                }
            }
    }

    override fun getFeed(id: UUID): Flow<RssFeed> {
        return feedDao.observeFeed(uuidMapper.fromUuid(id))
            .map(rssFeedMapper::toRssFeed)
    }

    override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeed>> {
        return feedDao.getPinnedAndRecentFeeds(maxCount).map {
            it.map { feed ->
                rssFeedMapper.toRssFeed(feed)
            }
        }
    }

    override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> {
        return opmlParser.parse(opml.iterator()).toImportData()
    }

    override suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData> {
        return opmlParser.parse(networkClient.get(url).bodyAsText().iterator()).toImportData()
    }

    private fun ParsedOpmlDocument?.toImportData(): List<OpmlFeedImportData> {
        return this?.feeds.orEmpty().map { feed ->
            OpmlFeedImportData(
                url = feed.xmlUrl,
                name = feed.title ?: feed.text,
            )
        }
    }

    override suspend fun addRssFeed(entry: RssFeedQueueEntry) {
        logger.d { "RSS import started queueId=${entry.id} source=${entry.url.sourceSummary()}" }
        try {
            feedImportFallbackParser.import(entry)
        } catch (e: Exception) {
            logger.e(e) {
                "RSS import failed queueId=${entry.id} source=${entry.url.sourceSummary()}"
            }
            throw e
        }
        logger.d {
            "RSS import succeeded queueId=${entry.id} source=${entry.url.sourceSummary()}"
        }
    }

    override suspend fun deleteFeed(id: UUID) {
        feedDao.deleteFeeds(listOf(uuidMapper.fromUuid(id)))
    }

    override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> {
        return runCatching {
            feedDao.setFeedPinned(id = uuidMapper.fromUuid(id), pinned = pinned)
        }
    }


    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host =
            withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "ContentFeedRepository"
    }
}
