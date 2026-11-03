package dev.shounakmulay.devpulse.core.domain.feed.discovery

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

class ObserveDiscoveryCollectionsTest {
    @Test
    fun `Given empty data When observing Then empty shelves and missing collection remain successful`() = runBlocking {
        val repository = Repository()
        assertEquals(emptyList(), ObserveFeedDiscoveryCollectionShelvesUseCase(repository, DispatchersForTest)().single().getOrThrow())
        assertNull(ObserveFeedDiscoveryCollectionUseCase(repository, DispatchersForTest)(UUID("collection")).single().getOrThrow())
    }

    @Test
    fun `Given construction or collection failure When observing Then the original failure reaches the caller`() = runBlocking {
        for (construction in listOf(true, false)) {
            val failure = IllegalStateException("unavailable")
            val repository = Repository(failure, construction)
            if (construction) {
                assertSame(failure, assertFailsWith<IllegalStateException> { ObserveFeedDiscoveryCollectionShelvesUseCase(repository, DispatchersForTest)() })
                assertSame(failure, assertFailsWith<IllegalStateException> { ObserveFeedDiscoveryCollectionUseCase(repository, DispatchersForTest)(UUID("collection")) })
            } else {
                assertSame(failure, ObserveFeedDiscoveryCollectionShelvesUseCase(repository, DispatchersForTest)().single().exceptionOrNull())
                assertSame(failure, ObserveFeedDiscoveryCollectionUseCase(repository, DispatchersForTest)(UUID("collection")).single().exceptionOrNull())
            }
        }
    }

    @Test
    fun `Given cancellation When observing Then cancellation propagates`() = runBlocking {
        val repository = Repository(CancellationException("cancelled"), false)
        assertFailsWith<CancellationException> { ObserveFeedDiscoveryCollectionShelvesUseCase(repository, DispatchersForTest)().collect() }
        assertFailsWith<CancellationException> { ObserveFeedDiscoveryCollectionUseCase(repository, DispatchersForTest)(UUID("collection")).collect() }
        Unit
    }

    private class Repository(private val failure: Throwable? = null, private val construction: Boolean = false) : FeedDiscoveryRepository {
        private fun <T> observe(value: T): Flow<T> {
            if (construction) failure?.let { throw it }
            return flow {
                failure?.let { throw it }
                emit(value)
            }
        }

        override fun observeCollectionShelves(previewCount: Int): Flow<List<FeedDiscoveryCollectionShelf>> = observe(emptyList())
        override fun observeCollection(collectionId: UUID): Flow<FeedDiscoveryCollection?> = observe(null)
        override fun observeFeed(feedId: UUID): Flow<FeedDiscoveryFeed?> = error("Unused")
        override fun observeFeeds(query: FeedDiscoveryQuery, pagingConfig: PagingConfig): Flow<PagingData<FeedDiscoveryFeed>> = error("Unused")
        override suspend fun isFeedDiscoveryInitialised(): Boolean = error("Unused")
        override suspend fun getDiscoverySeed(seedFilePath: String): DiscoverySeedData = error("Unused")
        override suspend fun initialiseWithSeed(seed: DiscoverySeedData) = error("Unused")
        override suspend fun refresh() = error("Unused")
    }

    private object DispatchersForTest : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }
}
