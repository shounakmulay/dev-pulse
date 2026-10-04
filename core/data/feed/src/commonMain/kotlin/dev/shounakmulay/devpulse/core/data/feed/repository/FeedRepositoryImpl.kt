package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.query.FtsQuerySanitizer
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.OpmlParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.model.ParsedOpmlDocument
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithSearch
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
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
    private val rssFeedMapper: RssFeedMapper,
    private val uuidMapper: UuidMapper,
    private val ftsQuerySanitizer: FtsQuerySanitizer
) : FeedRepository {
    override suspend fun upsertFeed(feed: RssFeed) {
        feedDao.upsertFeed(rssFeedMapper.toLocalRssFeed(feed))
    }

    override fun getFeedsListFlow(
        pagingConfig: PagingConfig,
        searchQuery: String?,
        pinnedOnly: Boolean,
    ): Flow<PagingData<RssFeedWithSearch>> {
        val query = searchQuery?.let(ftsQuerySanitizer::sanitize).orEmpty()
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                when {
                    query.isBlank() -> feedDao.getFeedPagingSource(pinnedOnly = pinnedOnly)
                    else -> feedDao.searchFeeds(query = query, pinnedOnly = pinnedOnly)
                }
            },
        ).flow.map { pagingData ->
            pagingData.map(rssFeedMapper::toRssFeedWithSearch)
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

    override suspend fun fetchRssFeed(entry: RssFeedQueueEntry): ParsedFeed {
        return feedImportFallbackParser.import(entry)
    }

    override suspend fun deleteFeed(id: UUID) {
        feedDao.deleteFeeds(listOf(uuidMapper.fromUuid(id)))
    }

    override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> {
        return runCatching {
            feedDao.setFeedPinned(id = uuidMapper.fromUuid(id), pinned = pinned)
        }
    }

    override suspend fun getFeedIdentityBySourceUrl(url: String): RssFeedIdentity? {
        return feedDao.getFeedIdentityBySourceUrl(url)?.let {
            rssFeedMapper.toRssIdentity(it)
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
