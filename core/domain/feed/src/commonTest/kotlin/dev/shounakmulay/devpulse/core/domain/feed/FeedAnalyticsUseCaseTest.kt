package dev.shounakmulay.devpulse.core.domain.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.DeleteFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractOpmlFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.FetchPostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.*
import dev.shounakmulay.devpulse.core.domain.models.post.*
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.events.EventEntry
import dev.shounakmulay.devpulse.core.logging.events.EventSink
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.*

class FeedAnalyticsUseCaseTest {
    private val events = mutableListOf<EventEntry>()
    private val posts = Posts()
    private val feeds = Feeds()
    private val bookmark = SetPostBookmarkedUseCase(
        postRepository = posts,
        dispatcherProvider = TestDispatchers,
        dpEvent = DPEvent,
    )
    private val delete = DeleteFeedUseCase(
        feedRepository = feeds,
        dispatcherProvider = TestDispatchers,
        dpEvent = DPEvent,
    )
    private val fetchContent = FetchPostContentUseCase(
        postRepository = posts,
        dispatcherProvider = TestDispatchers,
        dpEvent = DPEvent,
    )
    private val extractOpml = ExtractOpmlFeedsUseCase(
        feedRepository = feeds,
        dispatcherProvider = TestDispatchers,
        dpEvent = DPEvent,
    )
    private val id = UUID("private-post-or-feed-id")

    @BeforeTest
    fun setUp() {
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
    }

    @AfterTest
    fun tearDown() {
        DPEvent.configure(emptyList())
        stopKoin()
    }

    @Test
    fun `Given bookmark changes When persistence succeeds Then events contain only requested state`() = runBlocking {
        bookmark(id = id, bookmarked = true).getOrThrow()
        bookmark(id = id, bookmarked = false).getOrThrow()
        assertEquals(listOf(id to true, id to false), posts.bookmarks)
        assertEquals(listOf(
            EventEntry("post_bookmark_changed", mapOf("bookmarked" to "true")),
            EventEntry("post_bookmark_changed", mapOf("bookmarked" to "false")),
        ), events)
    }

    @Test
    fun `Given feed deletion When persistence succeeds Then event contains no feed identifier`() = runBlocking {
        delete(id).getOrThrow()
        assertEquals(listOf(id), feeds.deleted)
        assertEquals(listOf(EventEntry("feed_deleted")), events)
    }

    @Test
    fun `Given content fetch When successful Then result is retained and event contains no content`() = runBlocking {
        assertEquals(RssParsedPostContent("private HTML", "private markdown"), fetchContent(id).getOrThrow())
        assertEquals(listOf(EventEntry("post_content_fetch_completed")), events)
    }

    @Test
    fun `Given OPML extraction When successful Then events distinguish XML and URL without input data`() = runBlocking {
        val expected = listOf(OpmlFeedImportData("https://private.example/feed", "Private title"))
        assertEquals(expected, extractOpml.fromXml("private XML").getOrThrow())
        assertEquals(expected, extractOpml.fromUrl("https://private.example/opml").getOrThrow())
        assertEquals(listOf(
            EventEntry("opml_extraction_completed", mapOf("source" to "xml")),
            EventEntry("opml_extraction_completed", mapOf("source" to "url")),
        ), events)
    }

    @Test
    fun `Given repository failures When acting Then failures remain unchanged and no success event is emitted`() = runBlocking {
        val failure = IllegalStateException("private URL or content must not reach analytics")
        posts.failure = failure
        feeds.failure = failure
        assertSame(failure, bookmark(id = id, bookmarked = true).exceptionOrNull())
        assertSame(failure, delete(id).exceptionOrNull())
        assertSame(failure, fetchContent(id).exceptionOrNull())
        assertSame(failure, extractOpml.fromXml("private XML").exceptionOrNull())
        assertSame(failure, extractOpml.fromUrl("https://private.example/opml").exceptionOrNull())
        assertEquals(listOf(
            EventEntry("post_content_fetch_failed"),
            EventEntry("opml_extraction_failed", mapOf("source" to "xml")),
            EventEntry("opml_extraction_failed", mapOf("source" to "url")),
        ), events)
    }

    @Test
    fun `Given cancellation When acting Then cancellation escapes and no outcome event is emitted`() = runBlocking {
        val cancellation = CancellationException("cancelled")
        posts.failure = cancellation
        feeds.failure = cancellation
        val actions: List<suspend () -> Unit> = listOf(
            { bookmark(id = id, bookmarked = true) },
            { delete(id) },
            { fetchContent(id) },
            { extractOpml.fromXml("private XML") },
            { extractOpml.fromUrl("https://private.example/opml") },
        )
        for (action in actions) {
            assertSame(cancellation, assertFailsWith<CancellationException> { action() })
        }
        assertEquals(emptyList(), events)
    }

    private object TestDispatchers : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }

    private class Posts : PostRepository {
        var failure: Exception? = null
        val bookmarks = mutableListOf<Pair<UUID, Boolean>>()
        override suspend fun setPostBookmarked(id: UUID, bookmarked: Boolean) {
            failure?.let { throw it }
            bookmarks += id to bookmarked
        }
        override suspend fun fetchPostContentUseCase(postId: UUID, type: RssFeedPostContentType): RssParsedPostContent {
            failure?.let { throw it }
            return RssParsedPostContent("private HTML", "private markdown")
        }
        override fun getPost(id: UUID): Flow<RssPostWithFeedIdentityAndSearch> = error("unused")
        override suspend fun upsertPosts(posts: List<RssFeedPost>): Unit = error("unused")
        override fun observePosts(query: RssPostQuery, pagingConfig: PagingConfig): Flow<PagingData<RssPostWithFeedIdentityAndSearch>> = error("unused")
        override fun observeRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentityAndSearch>> = error("unused")
        override suspend fun setPostRead(id: UUID, isRead: Boolean): Unit = error("unused")
        override suspend fun getPostContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent? = error("unused")
        override suspend fun savePostContent(content: RssFeedPostContent): Unit = error("unused")
        override suspend fun getPostRssEncodedContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent? = error("unused")
        override suspend fun getPostDescription(postId: UUID): String? = error("unused")
        override suspend fun convertToMarkdown(html: String): RssParsedPostContent? = error("unused")
        override suspend fun getLatestPostPublishedTimeForFeed(feedId: UUID): Long? = error("unused")
    }

    private class Feeds : FeedRepository {
        var failure: Exception? = null
        val deleted = mutableListOf<UUID>()
        override suspend fun deleteFeed(id: UUID) {
            failure?.let { throw it }
            deleted += id
        }
        override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> {
            failure?.let { throw it }
            return listOf(OpmlFeedImportData("https://private.example/feed", "Private title"))
        }
        override suspend fun extractOpmlFeedsFromUrl(url: String) = extractOpmlFeeds(url)
        override suspend fun upsertFeed(feed: RssFeed): Unit = error("unused")
        override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("unused")
        override fun searchFeeds(query: String, snippetLength: Int): Flow<PagingData<RssFeedSearchResult>> = error("unused")
        override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeedWithUserState>> = error("unused")
        override fun getFeed(id: UUID): Flow<RssFeedWithUserState> = error("unused")
        override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("unused")
        override suspend fun fetchRssFeed(entry: RssFeedQueueEntry): ParsedFeed = error("unused")
        override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> = error("unused")
        override suspend fun getFeedIdentityBySourceUrl(url: String): RssFeedIdentity? = error("unused")
    }
}
