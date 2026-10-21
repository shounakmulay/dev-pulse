package dev.shounakmulay.devpulse.feature.feed.interactor.post

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPinnedAndRecentFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetRecentFeedItemsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPaginatedFeedPostsUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithUserState
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.ui.datetime.DateTimeStringConverter
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import kotlin.time.Instant
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostReadUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.models.post.RssParsedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class PostInteractorReadStateTest {
    @Test
    fun `Given overlapping actions for one post When setting read status Then neither choice is dropped`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        val release = CompletableDeferred<Unit>()
        fixture.repository.waitFor = release
        val first = async { fixture.interactor.setPostRead(postId = PostId, isRead = true) }
        val second = async { fixture.interactor.setPostRead(postId = PostId, isRead = false) }
        runCurrent()

        assertEquals(listOf(PostId to true, PostId to false), fixture.repository.commands)
        release.complete(Unit)
        first.await()
        second.await()
    }

    @Test
    fun `Given a failed read write When user repeats the action Then later write is accepted`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.repository.thrown = IllegalStateException("database busy")

        fixture.interactor.setPostRead(postId = PostId, isRead = true)
        fixture.repository.thrown = null
        fixture.interactor.setPostRead(postId = PostId, isRead = true)

        assertEquals(listOf(PostId to true, PostId to true), fixture.repository.commands)
    }

    @Test
    fun `Given cancelled read write When setting read status Then cancellation propagates`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.repository.thrown = CancellationException("screen left")

        assertEquals("screen left", assertFailsWith<CancellationException> {
            fixture.interactor.setPostRead(postId = PostId, isRead = true)
        }.message)
    }

    private class Fixture(dispatcher: CoroutineDispatcher) {
        val repository = Repository()
        private val dispatchers = object : DispatcherProvider {
            override val defaultDispatcher = dispatcher
            override val ioDispatcher = dispatcher
        }
        private val dates = object : DateTimeStringConverter {
            override fun getTimeElapsedOrDateString(instant: Instant): String = error("Unused")
        }
        private val recentPosts = GetRecentFeedItemsUseCase(repository, dispatchers)
        val interactor = PostInteractor(
            feedInteractor = FeedInteractor(
                extractInitialsUseCase = ExtractInitialsUseCase(),
                getPinnedAndRecentFeedsUseCase = GetPinnedAndRecentFeedsUseCase(UnusedFeeds, dispatchers),
                getRecentFeedItemsUseCase = recentPosts,
                dateTimeStringConverter = dates,
                dispatcherProvider = dispatchers,
            ),
            getRecentFeedItemsUseCase = recentPosts,
            getPaginatedFeedPostsUseCase = GetPaginatedFeedPostsUseCase(repository),
            dispatcherProvider = dispatchers,
            dateTimeStringConverter = dates,
            setPostReadUseCase = SetPostReadUseCase(repository, dispatchers),
        )
    }

    private object UnusedFeeds : FeedRepository {
        override suspend fun upsertFeed(feed: RssFeed) = error("Unused")
        override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
        override fun searchFeeds(query: String, snippetLength: Int): Flow<PagingData<RssFeedSearchResult>> = error("Unused")
        override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeedWithUserState>> = error("Unused")
        override fun getFeed(id: UUID): Flow<RssFeedWithUserState> = error("Unused")
        override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
        override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> = error("Unused")
        override suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData> = error("Unused")
        override suspend fun fetchRssFeed(entry: RssFeedQueueEntry): ParsedFeed = error("Unused")
        override suspend fun deleteFeed(id: UUID) = error("Unused")
        override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> = error("Unused")
        override suspend fun getFeedIdentityBySourceUrl(url: String): RssFeedIdentity? = error("Unused")
    }

    private class Repository : PostRepository {
        var waitFor: CompletableDeferred<Unit>? = null
        var thrown: Exception? = null
        val commands = mutableListOf<Pair<UUID, Boolean>>()
        override suspend fun setPostRead(id: UUID, isRead: Boolean) {
            commands += id to isRead
            thrown?.let { throw it }
            waitFor?.await()
        }
        override fun getPost(id: UUID): Flow<RssPostWithFeedIdentityAndSearch> = emptyFlow()
        override suspend fun upsertPosts(posts: List<RssFeedPost>) = error("Unused")
        override fun observePosts(query: RssPostQuery, pagingConfig: PagingConfig): Flow<PagingData<RssPostWithFeedIdentityAndSearch>> = emptyFlow()
        override fun observeRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentityAndSearch>> = emptyFlow()
        override suspend fun setPostBookmarked(id: UUID, bookmarked: Boolean) = error("Unused")
        override suspend fun fetchPostContentUseCase(postId: UUID, type: RssFeedPostContentType): RssParsedPostContent = error("Unused")
        override suspend fun getPostContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent? = error("Unused")
        override suspend fun savePostContent(content: RssFeedPostContent) = error("Unused")
        override suspend fun getPostRssEncodedContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent? = error("Unused")
        override suspend fun getPostDescription(postId: UUID): String? = error("Unused")
        override suspend fun convertToMarkdown(html: String): RssParsedPostContent? = error("Unused")
        override suspend fun getLatestPostPublishedTimeForFeed(feedId: UUID): Long? = error("Unused")
    }

    private companion object {
        val PostId = UUID("post-1")
    }
}
