package dev.shounakmulay.devpulse.core.domain.feed.discovery

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.*
import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.events.EventEntry
import dev.shounakmulay.devpulse.core.logging.events.EventSink
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.logging.log.DPLogConfig
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.*

class RefreshFeedDiscoveryUseCaseTest {
    private val events = mutableListOf<EventEntry>()

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
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Assert))
    }

    @AfterTest
    fun tearDown() {
        DPEvent.configure(emptyList())
        stopKoin()
    }

    @Test
    fun `Given successful fetch When refreshing Then completion is emitted once`() = runBlocking {
        assertTrue(useCase {}.invoke().isSuccess)
        assertEquals(listOf(EventEntry("discovery_fetch_completed")), events)
    }

    @Test
    fun `Given failed fetch When refreshing Then failure is retained and emitted once`() = runBlocking {
        val failure = IllegalStateException("private URL must not reach analytics")
        assertSame(failure, useCase { throw failure }.invoke().exceptionOrNull())
        assertEquals(listOf(EventEntry("discovery_fetch_failed")), events)
    }

    @Test
    fun `Given cancellation When refreshing Then no outcome event is emitted`() = runBlocking {
        assertFailsWith<CancellationException> { useCase { throw CancellationException() }.invoke() }
        assertEquals(emptyList(), events)
    }

    private fun useCase(refresh: suspend () -> Unit) = RefreshFeedDiscoveryUseCase(
        dpEvent = DPEvent,
        feedDiscoveryRepository = DiscoveryRepository(refresh),
        dispatcherProvider = object : DispatcherProvider {
            override val defaultDispatcher = Dispatchers.Unconfined
            override val ioDispatcher = Dispatchers.Unconfined
        },
        logger = DPLog.tag("test"),
    )

    private class DiscoveryRepository(private val onRefresh: suspend () -> Unit) : FeedDiscoveryRepository {
        override suspend fun refresh() = onRefresh()
        override suspend fun isFeedDiscoveryInitialised(): Boolean = error("unused")
        override suspend fun getDiscoverySeed(seedFilePath: String): DiscoverySeedData = error("unused")
        override suspend fun initialiseWithSeed(seed: DiscoverySeedData): Unit = error("unused")
        override fun observeCollectionShelves(previewCount: Int): Flow<List<FeedDiscoveryCollectionShelf>> = error("unused")
        override fun observeCollection(collectionId: UUID): Flow<FeedDiscoveryCollection?> = error("unused")
        override fun observeFeed(feedId: UUID): Flow<FeedDiscoveryFeed?> = error("unused")
        override fun observeFeeds(query: FeedDiscoveryQuery, pagingConfig: PagingConfig): Flow<PagingData<FeedDiscoveryFeed>> = error("unused")
    }
}
