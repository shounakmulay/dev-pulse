package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.events.EventEntry
import dev.shounakmulay.devpulse.core.logging.events.EventSink
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room3.RoomRawQuery
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.time.DefaultDateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.repository.*
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.*
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.domain.models.post.*
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.*
import dev.shounakmulay.devpulse.core.logging.log.DPLogConfig
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class RssFeedQueueExecutorTest {
    private val events = mutableListOf<EventEntry>()

    @AfterTest
    fun resetTelemetry() {
        DPEvent.configure(emptyList())
        stopKoin()
    }

    @kotlin.test.BeforeTest
    fun configureLogging() {
        startKoin { modules(module {
            single<AnalyticsConsentProvider> { object : AnalyticsConsentProvider {
                override fun getConsent() = DPAnalyticsConsent(true, true, true)
                override fun observeConsent() = flowOf(getConsent())
            } }
        }) }
        DPEvent.configure(listOf(object : EventSink {
            override fun isTrackable(entry: EventEntry) = true
            override fun track(entry: EventEntry) { events += entry }
        }))
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Assert))
    }

    @Test
    fun `Given lookup or fetch failure When draining Then failed entry does not block next entry`() = runBlocking {
        for (lookupFails in listOf(true, false)) {
            events.clear()
            val fixture = Fixture()
            fixture.queue.entries += listOf(entry(1), entry(2))
            if (lookupFails) fixture.feeds.lookup = { if (it.endsWith("/1")) error("lookup failed") else null }
            else fixture.feeds.fetch = { if (it.id == 1) error("invalid XML") else parsedFeed() }

            assertEquals(setOf(UUID("feed-2")), fixture.executor.process())
            assertEquals(listOf(EventEntry("feed_import_failed"), EventEntry("feed_import_completed")), events)
            assertEquals(listOf(RssFeedQueueStatus.FAILED, RssFeedQueueStatus.COMPLETED), fixture.queue.entries.map { it.status })
        }
    }

    @Test
    fun `Given cancellation When processing Then no failure is persisted and next entry waits`() = runBlocking {
        val fixture = Fixture()
        fixture.queue.entries += listOf(entry(1), entry(2))
        fixture.feeds.fetch = { throw CancellationException("worker stopped") }
        assertFailsWith<CancellationException> { fixture.executor.process() }
        assertEquals(0, fixture.sync.failed)
        assertEquals(emptyList(), events)
        assertEquals(RssFeedQueueStatus.QUEUED, fixture.queue.entries[1].status)
    }

    @Test
    fun `Given post metadata lookup failure When recording failed imports Then both entries become terminal`() = runBlocking {
        val fixture = Fixture()
        fixture.queue.entries += listOf(entry(1), entry(2))
        fixture.feeds.lookup = { RssFeedIdentity(UUID(it.substringAfterLast('/')), null, null, it, null, 0, 0) }
        fixture.posts.latest = { error("post lookup unavailable") }
        fixture.feeds.fetch = { if (it.id == 1) error("invalid XML") else parsedFeed() }
        fixture.executor.process()
        assertEquals(listOf(RssFeedQueueStatus.FAILED, RssFeedQueueStatus.FAILED), fixture.queue.entries.map { it.status })
    }

    @Test
    fun `Given failure metadata write fails When draining Then failed row is terminal and next entry completes`() = runBlocking {
        val fixture = Fixture()
        fixture.queue.entries += listOf(entry(1), entry(2))
        fixture.feeds.lookup = { if (it.endsWith("/1")) RssFeedIdentity(UUID("one"), null, null, it, null, 0, 0) else null }
        fixture.feeds.fetch = { if (it.id == 1) error("invalid XML") else parsedFeed() }
        fixture.metadata.failWrite = true
        assertEquals(setOf(UUID("feed-2")), fixture.executor.process())
        assertEquals(listOf(RssFeedQueueStatus.FAILED, RssFeedQueueStatus.COMPLETED), fixture.queue.entries.map { it.status })
    }

    @Test
    fun `Given repeated triggers during processing When another drain is requested Then each entry is fetched once`() = runBlocking {
        val fixture = Fixture()
        fixture.queue.entries += listOf(entry(1), entry(2))
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val fetched = mutableListOf<Int>()
        fixture.feeds.fetch = {
            fetched += it.id
            if (it.id == 1) {
                entered.complete(Unit)
                release.await()
            }
            parsedFeed()
        }
        val processing = launch { fixture.executor.process() }
        withTimeout(5_000) { entered.await() }
        repeat(10) { fixture.executor.triggerQueueProcessing() }
        release.complete(Unit)
        withTimeout(5_000) { processing.join() }
        assertEquals(listOf(1, 2), fetched)
        assertEquals(null, fixture.queue.getNextToProcess())
    }

    @Test
    fun `Given failure status cannot be saved When storage recovers Then a later drain resumes remaining entries`() = runBlocking {
        val fixture = Fixture()
        fixture.queue.entries += listOf(entry(1), entry(2))
        fixture.feeds.fetch = { if (it.id == 1) error("invalid XML") else parsedFeed() }
        fixture.sync.failStatusWrite = true
        assertFailsWith<IllegalStateException> { fixture.executor.process() }
        assertEquals(RssFeedQueueStatus.QUEUED, fixture.queue.entries[1].status)
        fixture.sync.failStatusWrite = false
        assertEquals(setOf(UUID("feed-2")), fixture.executor.process())
        assertEquals(listOf(RssFeedQueueStatus.FAILED, RssFeedQueueStatus.COMPLETED), fixture.queue.entries.map { it.status })
    }

    @Test
    fun `Given sync entries When processing success and failure Then no import events are emitted`() = runBlocking {
        val fixture = Fixture()
        fixture.queue.entries += listOf(entry(1), entry(2)).map { it.copy(actionType = RssFeedQueueActionType.SYNC) }
        fixture.feeds.fetch = { if (it.id == 1) error("fetch failed") else parsedFeed() }
        fixture.executor.process()
        assertEquals(emptyList(), events)
    }

    private class Fixture {
        val queue = Queue()
        val feeds = Feeds()
        val posts = Posts()
        val metadata = Metadata()
        val sync = Sync(queue, metadata)
        private val time = DefaultDateTimeProvider()
        private val ids = object : IdentityGenerator {
            override fun generateSortableId() = UUID("unused")
            override fun generateFingerprint(vararg strings: String) = strings.joinToString()
        }
        val executor = RssFeedQueueExecutorImpl(
            dpEvent = DPEvent,
            dispatcherProvider = TestDispatchers, feedRepository = feeds, postRepository = posts,
            feedSyncMetadataRepository = metadata, feedSyncRepository = sync, feedQueueRepository = queue,
            dateTimeProvider = time,
            parsedFeedProcessor = ParsedFeedProcessor(ids, UnusedPostDao, RssPostMapper(ids, time, UuidMapper()), UuidMapper(), DPLog.tag("test")),
            logger = DPLog.tag("test")
        )
    }

    private class Metadata : FeedSyncMetadataRepository {
        var failWrite = false
        override suspend fun getSyncMetadata(id: UUID): RssFeedSyncMetadata? = null
        override suspend fun getSyncMetadata(ids: List<UUID>): Map<UUID, RssFeedSyncMetadata> = emptyMap()
        override suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>> = emptyList()
        override suspend fun upsertSyncMetadata(metadata: RssFeedSyncMetadata) { if (failWrite) error("metadata write failed") }
    }

    private class Sync(private val queue: Queue, private val metadataRepository: Metadata) : FeedSyncRepository {
        var failed = 0
        var failStatusWrite = false
        override suspend fun importFeed(entry: RssFeedQueueEntry, parsedFeed: ParsedFeed, existingIdentity: RssFeedIdentity?) = RssFeed(
            id = UUID("feed-${entry.id}"), name = null, sourceUrl = entry.url, title = null, link = null,
            description = null, image = null, lastBuildDate = null, updatePeriod = null, youtubeChannel = null, createdAt = 0, updatedAt = 0
        )
        override suspend fun onImportCompleted(entry: RssFeedQueueEntry, feed: RssFeed, savedCount: Int, metadata: RssFeedSyncMetadata, removeStaleEntries: Boolean) {
            queue.updateQueueEntry(entry.copy(status = RssFeedQueueStatus.COMPLETED))
        }
        override suspend fun onImportFailed(entry: RssFeedQueueEntry, metadata: RssFeedSyncMetadata?) {
            if (failStatusWrite) error("queue status write failed")
            if (metadata != null) metadataRepository.upsertSyncMetadata(metadata)
            failed++
            queue.updateQueueEntry(entry.copy(status = RssFeedQueueStatus.FAILED, fetchAttempt = entry.fetchAttempt + 1))
        }
        override suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>> = error("Unused")
        override suspend fun importPosts(postsWithExistingIdentities: List<RssFeedPostWithExistingIdentity>) = error("Unused")
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
    }

    private object UnusedPostDao : FeedPostDao {
        override suspend fun upsertPost(post: LocalRssContentFeedPost) = error("Unused")
        override suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>) = error("Unused")
        override suspend fun deletePosts(posts: List<LocalRssContentFeedPost>) = error("Unused")
        override suspend fun getPost(id: LocalUUID): LocalRssContentFeedPost? = error("Unused")
        override fun observePost(id: LocalUUID): Flow<LocalRssPostWithFeedAndSearch> = error("Unused")
        override suspend fun getByFingerprints(fingerprints: Set<String>): Map<String, LocalRssContentFeedPostIdentitySlice> = error("Unused")
        override fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedAndSearch>> = error("Unused")
        override suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedAndSearch> = error("Unused")
        override suspend fun getLatestPostPublishedTimeForFeed(fromUuid: LocalUUID): Long? = error("Unused")
    }

    private object TestDispatchers : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }

    private companion object {
        fun parsedFeed() = ParsedFeed(ParsedFeedMetadata.build {}, emptyFlow(), emptyList())
        fun entry(id: Int) = RssFeedQueueEntry(
            id = id, feedId = null, url = "https://example.com/$id", name = null,
            feedType = RssFeedType.CONTENT, actionType = RssFeedQueueActionType.IMPORT,
            requestor = RssFeedQueueActionRequestor.USER, status = RssFeedQueueStatus.QUEUED, createdAt = 0, updatedAt = 0
        )
    }
    private class Feeds : FeedRepository {
        override suspend fun upsertFeed(feed: RssFeed) = error("Unused")
        override fun searchFeeds(query: String, snippetLength: Int): Flow<PagingData<RssFeedSearchResult>> = error("Unused")
        var lookup: suspend (String) -> RssFeedIdentity? = { null }
        var fetch: suspend (RssFeedQueueEntry) -> ParsedFeed = { ParsedFeed(ParsedFeedMetadata.build {}, emptyFlow(), emptyList()) }
        override suspend fun getFeedIdentityBySourceUrl(url: String) = lookup(url)
        override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")

        override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeedWithUserState>> = error("Unused")

        override fun getFeed(id: UUID): Flow<RssFeedWithUserState> = error("Unused")

        override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> = error("Unused")

        override suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData> {
            error("Unused")
        }

        override suspend fun fetchRssFeed(entry: RssFeedQueueEntry) = fetch(entry)

        override suspend fun deleteFeed(id: UUID) = error("Unused")

        override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> = error("Unused")

        override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
    }

    private class Posts : PostRepository {
        override suspend fun setPostRead(id: UUID, isRead: Boolean) = error("Unused")
        override fun getPost(id: UUID): Flow<RssPostWithFeedIdentityAndSearch> = error("Unused")
        override suspend fun upsertPosts(posts: List<RssFeedPost>) = error("Unused")
        override fun observePosts(query: RssPostQuery, pagingConfig: PagingConfig): Flow<PagingData<RssPostWithFeedIdentityAndSearch>> = error("Unused")
        override fun observeRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentityAndSearch>> = error("Unused")
        override suspend fun setPostBookmarked(id: UUID, bookmarked: Boolean) = error("Unused")
        override suspend fun fetchPostContentUseCase(postId: UUID, type: RssFeedPostContentType): RssParsedPostContent = error("Unused")
        override suspend fun getPostContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent? = error("Unused")
        override suspend fun savePostContent(content: RssFeedPostContent) = error("Unused")
        override suspend fun getPostRssEncodedContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent? = error("Unused")
        override suspend fun getPostDescription(postId: UUID): String? = error("Unused")
        override suspend fun convertToMarkdown(html: String): RssParsedPostContent? = error("Unused")
        var latest: suspend () -> Long? = { null }
        override suspend fun getLatestPostPublishedTimeForFeed(feedId: UUID): Long? = latest()
    }

    private class Queue : RssFeedQueueRepository {
        val entries = mutableListOf<RssFeedQueueEntry>()
        override fun observeQueue(status: Set<RssFeedQueueStatus>): Flow<List<RssFeedQueueEntry>> {
            return flowOf(entries.filter { it.status in status })
        }
        override suspend fun enqueue(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun enqueue(entries: List<RssFeedQueueEntry>) = error("Unused")
        override suspend fun updateQueueEntry(entry: RssFeedQueueEntry) { entries[entries.indexOfFirst { it.id == entry.id }] = entry }
        override suspend fun getNextToProcess() = entries.firstOrNull { it.status == RssFeedQueueStatus.QUEUED || it.status == RssFeedQueueStatus.PROCESSING }
        override fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>> = error("Unused")
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
        override fun observeQueuePagingData(status: Set<RssFeedQueueStatus>): Flow<PagingData<RssFeedQueueEntry>> = error("Unused")
    }

}
