package dev.shounakmulay.devpulse.core.domain.feed.queue

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ObserveFeedQueueUseCaseTest {
    @Test
    fun `Given queue values When either overload is collected Then the statuses and values are preserved`() = runBlocking {
        val repository = Repository { flowOf(emptyList()) }
        val useCase = ObserveFeedQueueUseCase(repository, TestDispatchers)
        assertEquals(emptyList(), useCase(RssFeedQueueStatus.QUEUED).single().getOrThrow())
        assertEquals(setOf(RssFeedQueueStatus.QUEUED), repository.statuses)
        assertEquals(emptyList(), useCase(setOf(RssFeedQueueStatus.PROCESSING)).single())
        assertEquals(setOf(RssFeedQueueStatus.PROCESSING), repository.statuses)
    }

    @Test
    fun `Given construction or upstream failure When observing results Then original failure is emitted`() = runBlocking {
        val failure = IllegalStateException("database unavailable")
        for (constructsFlow in listOf(true, false)) {
            val repository = Repository {
                if (constructsFlow) throw failure
                flow { throw failure }
            }
            val useCase = ObserveFeedQueueUseCase(repository, TestDispatchers)
            assertSame(failure, useCase(RssFeedQueueStatus.QUEUED).single().exceptionOrNull())
            if (constructsFlow) {
                assertSame(failure, assertFailsWith<IllegalStateException> { useCase(setOf(RssFeedQueueStatus.QUEUED)) })
            } else {
                assertEquals(emptyList(), useCase(setOf(RssFeedQueueStatus.QUEUED)).toList())
            }
        }
    }

    @Test
    fun `Given cancellation or downstream failure When collecting Then the exception escapes`() = runBlocking {
        val cancellation = CancellationException("cancelled")
        val cancelled = ObserveFeedQueueUseCase(Repository { flow { throw cancellation } }, TestDispatchers)
        assertFailsWith<CancellationException> { cancelled(RssFeedQueueStatus.QUEUED).collect() }
        val failure = IllegalStateException("consumer failed")
        val useCase = ObserveFeedQueueUseCase(Repository { flowOf(emptyList()) }, TestDispatchers)
        val thrown = assertFailsWith<IllegalStateException> {
            useCase(RssFeedQueueStatus.QUEUED).collect { throw failure }
        }
        assertTrue(thrown === failure || thrown.cause === failure)
    }

    private class Repository(private val observe: () -> Flow<List<RssFeedQueueEntry>>) : RssFeedQueueRepository {
        var statuses = emptySet<RssFeedQueueStatus>()
        override fun observeQueue(status: Set<RssFeedQueueStatus>): Flow<List<RssFeedQueueEntry>> {
            statuses = status
            return observe()
        }
        override suspend fun enqueue(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun enqueue(entries: List<RssFeedQueueEntry>) = error("Unused")
        override suspend fun updateQueueEntry(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun getNextToProcess(): RssFeedQueueEntry? = error("Unused")
        override fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>> = error("Unused")
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
        override fun observeQueuePagingData(status: Set<RssFeedQueueStatus>): Flow<PagingData<RssFeedQueueEntry>> = error("Unused")
    }

    private object TestDispatchers : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }
}
