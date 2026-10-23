package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingSource
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.data.db.dao.FeedUserStateDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedUserState
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedWithUserState
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedSearchResult
import dev.shounakmulay.devpulse.core.data.db.query.FtsQuerySanitizer
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.OpmlParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Instant

class FeedRepositoryImplTest {
    @Test
    fun `Given OPML text When extracting feeds Then parsed feeds are mapped to import data`() = runTest {
        val repository = createRepository()

        val result = repository.extractOpmlFeeds(OpmlFixture)

        assertEquals(2, result.size)
        assertEquals("https://example.com/feed.xml", result[0].url)
        assertEquals("Example Feed", result[0].name)
        assertEquals("https://loose.example.com/rss", result[1].url)
        assertEquals("Loose", result[1].name)
    }

    @Test
    fun `Given OPML URL When extracting feeds Then repository fetches once and maps response`() = runTest {
        val networkClient = FakeNetworkClient(OpmlFixture)
        val repository = createRepository(networkClient = networkClient)

        val result = repository.extractOpmlFeedsFromUrl("https://example.com/subscriptions.opml")

        assertEquals(listOf("https://example.com/subscriptions.opml"), networkClient.requestedUrls)
        assertEquals(2, result.size)
        assertEquals("https://example.com/feed.xml", result.first().url)
    }

    @Test
    fun `Given unsupported OPML text When extracting feeds Then empty import data is returned`() = runTest {
        val repository = createRepository()

        val result = repository.extractOpmlFeeds("<rss><channel /></rss>")

        assertEquals(emptyList(), result)
    }

    private fun createRepository(
        networkClient: FakeNetworkClient = FakeNetworkClient("")
    ): FeedRepositoryImpl {
        return FeedRepositoryImpl(
            feedImportFallbackParser = createFallbackParser(networkClient),
            opmlParser = OpmlParser(),
            networkClient = networkClient,
            feedDao = FakeFeedDao(),
            feedUserStateDao = FakeFeedUserStateDao(),
            rssFeedMapper = RssFeedMapper(
                identityGenerator = FakeIdentityGenerator(),
                dateTimeProvider = FakeDateTimeProvider(),
                uuidMapper = UuidMapper()
            ),
            uuidMapper = UuidMapper(),
            ftsQuerySanitizer = FtsQuerySanitizer(),
        )
    }

    private fun createFallbackParser(networkClient: DevPulseNetworkClient): FeedImportFallbackParser {
        return FeedImportFallbackParser(
            primaryParser = FakeFeedImportCandidate(),
            secondaryParser = FakeFeedImportCandidate(),
            networkClient = networkClient,
            logger = DPLog.tag("ContentFeedRepositoryImplTest")
        )
    }

    private class FakeFeedImportCandidate : FeedImportCandidate {
        override val id: String = "fake"

        override suspend fun parse(entry: RssFeedQueueEntry, xml: String) = error("Unused")

        override suspend fun parse(entry: RssFeedQueueEntry, iterator: CharIterator) = error("Unused")
    }

    private class FakeNetworkClient(
        private val text: String
    ) : DevPulseNetworkClient {
        val requestedUrls = mutableListOf<String>()

        override suspend fun get(url: String, headers: Map<String, String>): DevPulseNetworkResponse {
            requestedUrls += url
            return object : DevPulseNetworkResponse {
                override val status: Int = 200

                override fun header(name: String): String? = null

                override suspend fun bodyAsText(): String = text

                override suspend fun <T> body(type: KClass<*>, kotlinType: KType): T = error("Unused")
            }
        }
    }

    private class FakeFeedDao : FeedDao {
        override fun getFeedPagingSource(): PagingSource<Int, LocalRssFeedWithUserState> = error("Unused")
        override fun getPinnedFeedPagingSource(): PagingSource<Int, LocalRssFeedWithUserState> = error("Unused")
        override fun getPinnedAndRecentFeeds(count: Int): Flow<List<LocalRssFeedWithUserState>> = emptyFlow()
        override suspend fun getFeed(id: String): LocalRssFeed = error("Unused")
        override fun observeFeed(id: LocalUUID): Flow<LocalRssFeedWithUserState> = error("Unused")
        override suspend fun getFeedBySourceUrl(sourceUrl: String): LocalRssFeed? = error("Unused")
        override suspend fun getFeedIdentityBySourceUrl(sourceUrl: String): LocalRssFeedIdentitySlice? = error("Unused")
        override suspend fun upsertFeed(feed: LocalRssFeed) = Unit
        override suspend fun upsertFeeds(feeds: List<LocalRssFeed>) = Unit
        override suspend fun deleteFeeds(feeds: List<LocalUUID>) = Unit
        override fun searchFeeds(query: String, snippetLength: Int, hlStart: String, hlEnd: String): PagingSource<Int, LocalRssFeedSearchResult> = error("Unused")
    }

    private class FakeFeedUserStateDao : FeedUserStateDao {
        override suspend fun getState(feedId: LocalUUID): LocalRssFeedUserState? = error("Unused")
        override suspend fun ensureState(feedId: LocalUUID) = error("Unused")
        override suspend fun updatePinned(feedId: LocalUUID, pinned: Boolean): Int = error("Unused")
        override suspend fun updateLastOpenedAt(feedId: LocalUUID, lastOpenedAt: Long?): Int = error("Unused")
    }

    private class FakeIdentityGenerator : IdentityGenerator {
        override fun generateSortableId(): UUID = UUID("id")

        override fun generateFingerprint(vararg strings: String): String = strings.joinToString()
    }

    private class FakeDateTimeProvider : DateTimeProvider {
        override fun parse(string: String): Instant? = null

        override fun getTimeElapsed(instant: Instant): Duration = Duration.ZERO

        override fun now(): Instant = Instant.fromEpochMilliseconds(0)

        override fun timeInFuture(duration: Duration): Long = duration.inWholeMilliseconds
        override fun nowEpochMilliseconds(): Long = 0

        override fun today(): LocalDate = LocalDate(2026, 1, 1)
    }

    private companion object {
        val OpmlFixture = """
            <opml version="2.0">
                <head><title>Subscriptions</title></head>
                <body>
                    <outline text="Dev">
                        <outline
                            text="Example"
                            title="Example Feed"
                            type="rss"
                            xmlUrl="https://example.com/feed.xml"
                            htmlUrl="https://example.com"
                        />
                    </outline>
                    <outline title="Loose" type="rss" xmlUrl="https://loose.example.com/rss" />
                </body>
            </opml>
        """.trimIndent()
    }
}
