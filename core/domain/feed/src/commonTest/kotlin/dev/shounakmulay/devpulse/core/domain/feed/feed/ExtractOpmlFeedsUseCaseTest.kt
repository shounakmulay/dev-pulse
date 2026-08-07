package dev.shounakmulay.devpulse.core.domain.feed.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ExtractOpmlFeedsUseCaseTest {
    @Test
    fun `Given URL repository failure When extracting OPML feeds Then failure is returned`() = runBlocking {
        val failure = IllegalStateException("boom")
        val useCase = ExtractOpmlFeedsUseCase(
            feedRepository = FakeFeedRepository(failure = failure),
            dispatcherProvider = TestDispatcherProvider
        )

        val result = useCase.fromUrl("https://example.com/subscriptions.opml")

        assertEquals(true, result.isFailure)
        assertSame(failure, result.exceptionOrNull())
    }

    private class FakeFeedRepository(
        private val failure: Exception
    ) : FeedRepository {
        override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>> = error("Unused")

        override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeed>> = error("Unused")

        override fun getFeed(id: UUID): Flow<RssFeed> = error("Unused")

        override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> = error("Unused")

        override suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData> {
            throw failure
        }

        override suspend fun addRssFeed(entry: RssFeedQueueEntry) = error("Unused")

        override suspend fun deleteFeed(id: UUID) = error("Unused")

        override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> = error("Unused")

        override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeed>> = error("Unused")
    }

    private object TestDispatcherProvider : DispatcherProvider {
        override val defaultDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
        override val ioDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    }
}
