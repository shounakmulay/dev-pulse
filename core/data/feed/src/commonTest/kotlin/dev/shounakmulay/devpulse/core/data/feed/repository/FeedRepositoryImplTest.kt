package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room3.RoomRawQuery
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.paging.FeedPostPagingSourceProvider
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostQueryMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.OpmlParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.logging.DPLog
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
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
            feedPostDao = FakeFeedPostDao(),
            feedPostPagingSourceProvider = FakeFeedPostPagingSourceProvider(),
            rssFeedMapper = RssFeedMapper(
                identityGenerator = FakeIdentityGenerator(),
                dateTimeProvider = FakeDateTimeProvider(),
                uuidMapper = UuidMapper()
            ),
            rssPostMapper = RssPostMapper(
                idGenerator = FakeIdentityGenerator(),
                dateTimeProvider = FakeDateTimeProvider(),
                uuidMapper = UuidMapper()
            ),
            logger = DPLog.tag("ContentFeedRepositoryImplTest"),
            rssPostQueryMapper = RssPostQueryMapper()
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

        override suspend fun parse(entry: RssFeedQueueEntry, xml: String) = Unit

        override suspend fun parse(entry: RssFeedQueueEntry, iterator: CharIterator) = Unit
    }

    private class FakeNetworkClient(
        private val text: String
    ) : DevPulseNetworkClient {
        val requestedUrls = mutableListOf<String>()

        override suspend fun get(url: String, headers: Map<String, String>): DevPulseNetworkResponse {
            requestedUrls += url
            return DevPulseNetworkResponse { text }
        }
    }

    private class FakeFeedDao : FeedDao {
        override fun getFeedPagingSource(): PagingSource<Int, LocalRssFeed> = error("Unused")

        override fun getPinnedFeedPagingSource(): PagingSource<Int, LocalRssFeed> = error("Unused")

        override fun getPinnedAndRecentFeeds(count: Int): Flow<List<LocalRssFeed>> = emptyFlow()

        override suspend fun getFeed(id: String): LocalRssFeed = error("Unused")

        override fun observeFeed(id: String): Flow<LocalRssFeed> = error("Unused")

        override suspend fun getFeedBySourceUrl(sourceUrl: String): LocalRssFeed? = error("Unused")

        override suspend fun setFeedPinned(id: String, pinned: Boolean) = Unit

        override suspend fun getFeedIdentityBySourceUrl(sourceUrl: String): LocalRssFeedIdentitySlice? {
            return error("Unused")
        }

        override suspend fun upsertFeed(feed: LocalRssFeed) = Unit

        override suspend fun upsertFeeds(feeds: List<LocalRssFeed>) = Unit

        override suspend fun deleteFeeds(feeds: List<String>) = Unit
    }

    private class FakeFeedPostDao : FeedPostDao {
        override suspend fun upsertPost(post: LocalRssContentFeedPost) = Unit

        override suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>) = Unit

        override suspend fun deletePosts(posts: List<LocalRssContentFeedPost>) = Unit

        override suspend fun getPost(id: String): LocalRssContentFeedPost = error("Unused")

        override suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean) = Unit

        override suspend fun getByFingerprints(
            fingerprints: Set<String>
        ): Map<String, LocalRssContentFeedPostIdentitySlice> = emptyMap()

        override suspend fun getPostsForFeed(feedId: String): List<LocalRssContentFeedPost> = emptyList()

        override fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedAndSearch>> {
            return emptyFlow()
        }

        override fun observePost(id: String): Flow<LocalRssPostWithFeedAndSearch> {
            return emptyFlow()
        }

        override suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedAndSearch> {
            return emptyList()
        }

        override suspend fun getInitialPage(limit: Int): List<LocalRssContentFeedPost> = emptyList()

        override suspend fun getPageAfter(
            id: String,
            limit: Int
        ): List<LocalRssContentFeedPost> = emptyList()

        override suspend fun getRefreshPageAround(
            id: String,
            limit: Int
        ): List<LocalRssContentFeedPost> = emptyList()

        override suspend fun upsertPostCategories(categories: List<LocalRssPostCategory>) = Unit
        override suspend fun deletePostCategories(postId: String) = Unit
        override suspend fun deletePostCategories(postIds: Set<String>) = Unit
        override suspend fun getPageBeforeQuery(id: String, limit: Int): List<LocalRssContentFeedPost> = emptyList()
    }

    private class FakeFeedPostPagingSourceProvider : FeedPostPagingSourceProvider {
        override fun getFeedPostPagingSource(
            query: LocalFeedPostQuery
        ): PagingSource<FeedPostCursor, LocalRssPostWithFeedAndSearch> {
            return object : PagingSource<FeedPostCursor, LocalRssPostWithFeedAndSearch>() {
                override fun getRefreshKey(
                    state: PagingState<FeedPostCursor, LocalRssPostWithFeedAndSearch>
                ): FeedPostCursor? = null

                override suspend fun load(
                    params: LoadParams<FeedPostCursor>
                ): LoadResult<FeedPostCursor, LocalRssPostWithFeedAndSearch> {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }
            }
        }
    }

    private class FakeIdentityGenerator : IdentityGenerator {
        override fun generateSortableId(): String = "id"

        override fun generateFingerprint(vararg strings: String): String = strings.joinToString()
    }

    private class FakeDateTimeProvider : DateTimeProvider {
        override fun parse(string: String): Instant? = null

        override fun getTimeElapsed(instant: Instant): Duration = Duration.ZERO

        override fun now(): Instant = Instant.fromEpochMilliseconds(0)

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
