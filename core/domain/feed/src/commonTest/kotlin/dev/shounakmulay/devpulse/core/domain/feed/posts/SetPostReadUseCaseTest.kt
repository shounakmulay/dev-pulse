package dev.shounakmulay.devpulse.core.domain.feed.posts

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.models.post.RssParsedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SetPostReadUseCaseTest {
    @Test
    fun `Given requested read status When setting it Then repository receives the exact post and value`() = runBlocking {
        val writes = mutableListOf<Pair<UUID, Boolean>>()
        val repository = Repository { id, isRead -> writes += id to isRead }
        val useCase = SetPostReadUseCase(repository, TestDispatchers)

        for (isRead in listOf(true, false)) {
            assertEquals(Unit, useCase(id = PostId, isRead = isRead).getOrThrow())
        }

        assertEquals(listOf(PostId to true, PostId to false), writes)
    }

    @Test
    fun `Given repository failure When setting read state Then Result preserves the original exception`() = runBlocking {
        val failure = IllegalStateException("database busy")
        val useCase = SetPostReadUseCase(Repository { _, _ -> throw failure }, TestDispatchers)

        val result = useCase(id = PostId, isRead = true)

        assertTrue(result.isFailure)
        assertSame(failure, result.exceptionOrNull())
    }

    @Test
    fun `Given injected IO dispatcher When setting read state Then repository executes in that context`() = runBlocking {
        var observedDispatcher: ContinuationInterceptor? = null
        val repository = Repository { _, _ ->
            observedDispatcher = currentCoroutineContext()[ContinuationInterceptor]
            Unit
        }

        val result = SetPostReadUseCase(repository, TestDispatchers)(id = PostId, isRead = true)

        assertEquals(Unit, result.getOrThrow())
        assertSame(TestDispatchers.ioDispatcher, observedDispatcher)
    }

    @Test
    fun `Given repository cancellation When setting read state Then cancellation escapes the Result boundary`() = runBlocking {
        val useCase = SetPostReadUseCase(Repository { _, _ -> throw CancellationException("reader closed") }, TestDispatchers)

        val thrown = assertFailsWith<CancellationException> { useCase(id = PostId, isRead = true) }

        assertEquals("reader closed", thrown.message)
    }

    @Test
    fun `Given suspended repository work When caller cancels Then work stops without returning a failure Result`() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val repository = Repository { _, _ -> entered.complete(Unit); awaitCancellation() }
        var result: Result<Unit>? = null
        val useCase = SetPostReadUseCase(repository, TestDispatchers)
        val caller = launch { result = useCase(id = PostId, isRead = true) }
        withTimeout(5_000) { entered.await() }

        caller.cancelAndJoin()

        assertTrue(caller.isCancelled)
        assertNull(result)
    }

    private class Repository(private val onRead: suspend (UUID, Boolean) -> Unit) : PostRepository {
        override suspend fun setPostRead(id: UUID, isRead: Boolean) = onRead(id, isRead)
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
        override suspend fun getLatestPostPublishedTimeForFeed(feedId: UUID): Long? = error("Unused")
    }

    private object TestDispatchers : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Default
        override val ioDispatcher = Dispatchers.Unconfined
    }

    private companion object {
        val PostId = UUID("post-1")
    }
}
