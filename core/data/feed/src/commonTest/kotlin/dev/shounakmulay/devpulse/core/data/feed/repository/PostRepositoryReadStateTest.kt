package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingSource
import androidx.room3.RoomRawQuery
import com.fleeksoft.ksoup.Ksoup
import dev.shounakmulay.devpulse.bridge.markdownconverter.HtmlToMarkdownConverter
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.dao.PostContentDao
import dev.shounakmulay.devpulse.core.data.db.dao.PostUserStateDao
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssPostContentType
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContentFts
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostUserState
import dev.shounakmulay.devpulse.core.data.db.paging.FeedPostPagingSourceProvider
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionScope
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostContentMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostQueryMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.html.ArticleFtsContentParser
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkResponse
import dev.shounakmulay.devpulse.core.network.DevPulseUrlHelper
import dev.shounakmulay.devpulse.readability.ArticleExtractor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Duration
import kotlin.time.Instant

class PostRepositoryReadStateTest {
    @Test
    fun `Given no parent When changing read state Then creates no orphan state`() = runTest {
        val fixture = Fixture(parentExists = false)

        fixture.repository.setPostRead(UUID(PostId.value), isRead = true)

        assertNull(fixture.state.row)
    }

    @Test
    fun `Given no state When marking read and unread Then persists the latest choice`() = runTest {
        val fixture = Fixture()

        fixture.repository.setPostRead(UUID(PostId.value), isRead = true)
        assertEquals(LocalRssPostUserState(PostId, isRead = true), fixture.state.row)
        fixture.repository.setPostRead(UUID(PostId.value), isRead = false)
        assertEquals(LocalRssPostUserState(PostId, isRead = false), fixture.state.row)
    }

    @Test
    fun `Given bookmarked post When read status repeats or changes Then bookmark survives`() = runTest {
        val fixture = Fixture(row = LocalRssPostUserState(PostId, isRead = true, bookmarked = true))

        fixture.repository.setPostRead(UUID(PostId.value), isRead = true)
        assertEquals(LocalRssPostUserState(PostId, isRead = true, bookmarked = true), fixture.state.row)
        fixture.repository.setPostRead(UUID(PostId.value), isRead = false)
        assertEquals(LocalRssPostUserState(PostId, isRead = false, bookmarked = true), fixture.state.row)
        fixture.repository.setPostBookmarked(UUID(PostId.value), bookmarked = false)
        assertEquals(LocalRssPostUserState(PostId, isRead = false, bookmarked = false), fixture.state.row)
    }

    @Test
    fun `Given failed write When setting read status Then original failure propagates without a state change`() = runTest {
        val fixture = Fixture()
        val failure = IllegalStateException("database busy")
        fixture.state.readFailure = failure

        assertSame(failure, assertFailsWith<IllegalStateException> {
            fixture.repository.setPostRead(UUID(PostId.value), isRead = true)
        })
        assertNull(fixture.state.row)
        fixture.state.readFailure = null
        fixture.repository.setPostRead(UUID(PostId.value), isRead = false)
        assertEquals(LocalRssPostUserState(PostId), fixture.state.row)
    }

    @Test
    fun `Given cancelled write When setting read status Then cancellation propagates`() = runTest {
        val fixture = Fixture()
        val cancellation = CancellationException("screen left")
        fixture.state.readFailure = cancellation

        assertSame(cancellation, assertFailsWith<CancellationException> {
            fixture.repository.setPostRead(UUID(PostId.value), isRead = true)
        })
        assertNull(fixture.state.row)
    }

    private class Fixture(
        parentExists: Boolean = true,
        row: LocalRssPostUserState? = null,
    ) {
        val posts = Posts(parentExists)
        val state = UserState(row) { posts.parentExists }
        val repository = PostRepositoryImpl(
            articleExtractor = ArticleExtractor(),
            articleFtsContentParser = ArticleFtsContentParser(Ksoup, DispatchersForTest),
            feedPostDao = posts,
            postUserStateDao = state,
            postContentDao = UnusedContentDao,
            feedPostPagingSourceProvider = UnusedPagingProvider,
            rssPostQueryMapper = RssPostQueryMapper(),
            rssPostMapper = RssPostMapper(idGenerator = Ids, dateTimeProvider = Time, uuidMapper = UuidMapper()),
            rssFeedMapper = RssFeedMapper(identityGenerator = Ids, dateTimeProvider = Time, uuidMapper = UuidMapper()),
            rssPostContentMapper = RssPostContentMapper(UuidMapper()),
            uuidMapper = UuidMapper(),
            dispatcherProvider = DispatchersForTest,
            devPulseNetworkClient = UnusedNetwork,
            devPulseUrlHelper = UnusedUrls,
            markdownConverter = UnusedMarkdown,
            transactionAccessor = Transactions,
        )
    }

    private class UserState(var row: LocalRssPostUserState?, val parentExists: () -> Boolean) : PostUserStateDao {
        var readFailure: Exception? = null
        override suspend fun getState(postId: LocalUUID): LocalRssPostUserState? {
            readFailure?.let { throw it }
            return row?.takeIf { it.postId == postId }
        }
        override suspend fun ensureState(postId: LocalUUID) {
            readFailure?.let { throw it }
            if (parentExists() && row == null) row = LocalRssPostUserState(postId)
        }
        override suspend fun updateBookmarked(postId: LocalUUID, bookmarked: Boolean): Int {
            val current = row?.takeIf { it.postId == postId && it.bookmarked != bookmarked } ?: return 0
            row = current.copy(bookmarked = bookmarked)
            return 1
        }
        override suspend fun updateRead(postId: LocalUUID, isRead: Boolean): Int {
            val current = row?.takeIf { it.postId == postId && it.isRead != isRead } ?: return 0
            row = current.copy(isRead = isRead)
            return 1
        }
    }

    private class Posts(var parentExists: Boolean) : FeedPostDao {
        override suspend fun getPost(id: LocalUUID): LocalRssContentFeedPost? = if (parentExists && id == PostId) post else null
        override suspend fun upsertPost(post: LocalRssContentFeedPost) = error("Unused")
        override suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>) = error("Unused")
        override suspend fun deletePosts(posts: List<LocalRssContentFeedPost>) = error("Unused")
        override fun observePost(id: LocalUUID): Flow<LocalRssPostWithFeedAndSearch> = emptyFlow()
        override suspend fun getByFingerprints(fingerprints: Set<String>): Map<String, LocalRssContentFeedPostIdentitySlice> = error("Unused")
        override fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedAndSearch>> = emptyFlow()
        override suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedAndSearch> = error("Unused")
        override suspend fun getLatestPostPublishedTimeForFeed(fromUuid: LocalUUID): Long? = error("Unused")
    }

    private object Transactions : DevPulseDatabaseTransactionAccessor {
        override suspend fun <T> readTransaction(block: suspend DevPulseDatabaseTransactionScope<T>.() -> T): T = scope<T>().block()
        override suspend fun <T> writeTransaction(block: suspend DevPulseDatabaseTransactionScope<T>.() -> T): T = scope<T>().block()
        private fun <T> scope() = object : DevPulseDatabaseTransactionScope<T> {
            override suspend fun rollback(result: T) = error("Unused")
        }
    }

    private object DispatchersForTest : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }

    private object Ids : IdentityGenerator {
        override fun generateSortableId() = UUID("generated")
        override fun generateFingerprint(vararg strings: String) = strings.joinToString()
    }

    private object Time : DateTimeProvider {
        override fun parse(string: String): Instant? = null
        override fun getTimeElapsed(instant: Instant): Duration = Duration.ZERO
        override fun now(): Instant = Instant.fromEpochMilliseconds(0L)
        override fun timeInFuture(duration: Duration): Long = duration.inWholeMilliseconds
        override fun nowEpochMilliseconds(): Long = 0L
        override fun today(): LocalDate = LocalDate(2026, 1, 1)
    }

    private object UnusedPagingProvider : FeedPostPagingSourceProvider {
        override fun getFeedPostPagingSource(query: LocalFeedPostQuery): PagingSource<FeedPostCursor, LocalRssPostWithFeedAndSearch> = error("Unused")
    }

    private object UnusedContentDao : PostContentDao {
        override suspend fun upsertPostContent(feedContent: LocalRssPostContent) = error("Unused")
        override suspend fun insertPostContentFts(feedContentFts: LocalRssPostContentFts) = error("Unused")
        override suspend fun deletePostContentFts(postId: LocalUUID) = error("Unused")
        override suspend fun upsertPostContents(feedContents: List<LocalRssPostContent>) = error("Unused")
        override suspend fun getPostContents(postId: LocalUUID, type: LocalRssPostContentType): LocalRssPostContent? = error("Unused")
    }

    private object UnusedNetwork : DevPulseNetworkClient {
        override suspend fun get(url: String, headers: Map<String, String>): DevPulseNetworkResponse = error("Unused")
    }

    private object UnusedUrls : DevPulseUrlHelper {
        override fun getBaseUrl(url: String): String = error("Unused")
    }

    private object UnusedMarkdown : HtmlToMarkdownConverter {
        override suspend fun convert(html: String): String? = error("Unused")
    }

    private companion object {
        val PostId = LocalUUID("post-1")
        val post = LocalRssContentFeedPost(
            id = PostId,
            feedId = LocalUUID("feed-1"),
            fingerprint = "fingerprint-1",
            guid = null,
            title = "A post",
            link = null,
            pubDate = null,
            description = null,
            content = null,
            image = null,
            audio = null,
            video = null,
            categories = "",
            commentsUrl = null,
            youtubeData = null,
            rawEnclosure = null,
            createdAt = 0L,
            updatedAt = 0L,
        )
    }
}
