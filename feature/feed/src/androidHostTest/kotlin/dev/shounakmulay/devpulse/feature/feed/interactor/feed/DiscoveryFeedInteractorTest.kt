package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.feed.discovery.GetPaginatedFeedDiscoveryFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.EnqueueFeedDiscoveryImportUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.ObserveFeedDiscoveryCollectionShelvesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.ObserveFeedDiscoveryCollectionUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.ObserveFeedDiscoveryFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedImportsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryImportState
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoverySearchHighlights
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.ui.text.AnnotatedStringTextResource
import dev.shounakmulay.devpulse.core.ui.text.SimpleTextResource
import dev.shounakmulay.devpulse.core.ui.text.TextResourceSpan
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeedImportState
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.service.FeedImportBackgroundProcessLauncher
import dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui.BrowseFeedsScreenEvent
import dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui.BrowseFeedsViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.datetime.LocalDate
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [35])
class DiscoveryFeedInteractorTest {
    @Test
    fun `Given available feed When import is requested Then content import payload is queued`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        fixture.repository.feedState(FeedId).value = feed()

        assertEquals(FeedDiscoveryImportState.Queued, fixture.enqueueImport(FeedId).getOrThrow())

        assertEquals(listOf("enqueue"), fixture.events)
        val entry = fixture.queue.entries.single()
        assertEquals("https://example.com/rss", entry.url)
        assertEquals("Kotlin Weekly", entry.name)
        assertEquals(RssFeedType.CONTENT, entry.feedType)
        assertEquals(RssFeedQueueActionType.IMPORT, entry.actionType)
        assertEquals(RssFeedQueueActionRequestor.USER, entry.requestor)
        assertEquals(RssFeedQueueStatus.QUEUED, entry.status)
        assertEquals(1234L, entry.createdAt)
        assertEquals(emptyList(), entry.tags)
        assertEquals(emptyList(), entry.folders)
    }

    @Test
    fun `Given added or pending feed When added Then existing import is left unchanged`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        for (state in listOf(FeedDiscoveryImportState.Added, FeedDiscoveryImportState.Adding, FeedDiscoveryImportState.Queued)) {
            fixture.repository.feedState(FeedId).value = feed().copy(importState = state)
            assertEquals(state, fixture.enqueueImport(FeedId).getOrThrow())
        }
        assertEquals(emptyList(), fixture.queue.entries)
        assertEquals(emptyList(), fixture.events)

        fixture.repository.feedState(FeedId).value = feed().copy(importState = FeedDiscoveryImportState.Failed)
        fixture.enqueueImport(FeedId).getOrThrow()
        assertEquals("https://example.com/rss", fixture.queue.entries.single().url)
        assertEquals(listOf("enqueue"), fixture.events)
    }

    @Test
    fun `Given enqueue fails When import is requested Then original failure is returned without a queue write`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        fixture.repository.feedState(FeedId).value = feed()
        val failure = IllegalStateException("queue unavailable")
        fixture.queue.failure = failure

        assertSame(failure, fixture.enqueueImport(FeedId).exceptionOrNull())
        assertEquals(listOf("enqueue"), fixture.events)
        assertEquals(emptyList(), fixture.queue.entries)
    }

    @Test
    fun `Given enqueue is cancelled When import is requested Then cancellation propagates`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        fixture.repository.feedState(FeedId).value = feed()
        fixture.queue.failure = CancellationException("cancelled import")

        assertFailsWith<CancellationException> { fixture.enqueueImport(FeedId) }
        assertEquals(listOf("enqueue"), fixture.events)
    }

    @Test
    fun `Given missing feed When added Then failure is returned without enqueuing`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))

        assertTrue(fixture.enqueueImport(FeedId).isFailure)
        assertEquals(emptyList(), fixture.events)
    }

    @Test
    fun `Given selected feed When status changes or selection switches Then details follow only current feed`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        val otherId = UUID("other-feed")
        fixture.repository.feedState(FeedId).value = feed()
        fixture.repository.feedState(otherId).value = feed().copy(id = otherId, title = "Compose News")
        val selected = MutableStateFlow<UUID?>(FeedId)
        val observed = mutableListOf<Pair<UUID?, UIDiscoveryFeed?>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.interactor.observeSelectedFeed(selected).collect { observed += it }
        }
        runCurrent()
        assertEquals("Kotlin Weekly", observed.last().second?.title)

        fixture.repository.feedState(FeedId).value = feed().copy(importState = FeedDiscoveryImportState.Queued)
        runCurrent()
        assertEquals(UIDiscoveryFeedImportState.Queued, observed.last().second?.importState)

        selected.value = otherId
        runCurrent()
        assertEquals(otherId, observed.last().first)
        assertEquals("Compose News", observed.last().second?.title)
        val afterSwitch = observed.size
        fixture.repository.feedState(FeedId).value = feed().copy(importState = FeedDiscoveryImportState.Added)
        runCurrent()
        assertEquals(afterSwitch, observed.size)

        fixture.repository.feedState(otherId).value = null
        runCurrent()
        assertEquals(otherId to null, observed.last())
        selected.value = null
        runCurrent()
        assertEquals(null to null, observed.last())
    }

    @Test
    fun `Given collection and search queries When switched or cleared Then only current paging stream remains active`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        val collection = FeedDiscoveryQuery.Collection(UUID("collection"))
        val search = FeedDiscoveryQuery.Search("Kotlin", UUID("collection"))
        val global = FeedDiscoveryQuery.Search("Compose")
        val queries = MutableStateFlow<FeedDiscoveryQuery?>(collection)
        var emissions = 0
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.interactor.getFeeds(queries).collect { emissions++ }
        }
        runCurrent()
        assertEquals(listOf<FeedDiscoveryQuery>(collection), fixture.repository.requests.map { it.query })
        assertEquals(setOf<FeedDiscoveryQuery>(collection), fixture.repository.activeQueries)
        val config = fixture.repository.requests.single().config
        assertEquals(20, config.pageSize)
        assertEquals(40, config.initialLoadSize)
        assertEquals(5, config.prefetchDistance)
        assertEquals(false, config.enablePlaceholders)

        queries.value = search
        runCurrent()
        assertEquals(listOf(collection, search), fixture.repository.requests.map { it.query })
        assertEquals(setOf<FeedDiscoveryQuery>(search), fixture.repository.activeQueries)
        assertEquals(listOf<FeedDiscoveryQuery>(collection), fixture.repository.cancelledQueries)

        queries.value = global
        runCurrent()
        assertEquals(listOf(collection, search, global), fixture.repository.requests.map { it.query })
        assertEquals(setOf<FeedDiscoveryQuery>(global), fixture.repository.activeQueries)

        val beforeClear = emissions
        queries.value = null
        runCurrent()
        assertTrue(emissions > beforeClear)
        assertEquals(emptySet(), fixture.repository.activeQueries)
        assertEquals(listOf(collection, search, global), fixture.repository.cancelledQueries)
        assertEquals(3, fixture.repository.requests.size)
    }

    @Test
    fun `Given browse search events When query changes or clears Then ViewModel owns eligibility and debounce`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        startKoin { modules(module { factory { SavedStateHandle() } }) }
        val fixture = Fixture(dispatcher)
        val viewModel = BrowseFeedsViewModel(
            discoveryFeedInteractor = fixture.interactor,
            enqueueFeedDiscoveryImportUseCase = fixture.enqueueImport,
            feedImportBackgroundProcessLauncher = object : FeedImportBackgroundProcessLauncher {
                override suspend fun launch() = Unit
            },
            logger = DPLog.tag("DiscoverySearchTest"),
        )
        try {
            backgroundScope.launch(dispatcher) { viewModel.feeds.collect() }
            runCurrent()
            viewModel.onEvent(BrowseFeedsScreenEvent.OnSearchQueryChanged("  Kotlin  "))
            advanceTimeBy(299)
            runCurrent()
            assertEquals(emptyList(), fixture.repository.requests)

            viewModel.onEvent(BrowseFeedsScreenEvent.OnSearchQueryChanged("Compose"))
            advanceTimeBy(300)
            runCurrent()
            assertEquals(
                listOf<FeedDiscoveryQuery>(FeedDiscoveryQuery.Search("Compose")),
                fixture.repository.requests.map { it.query },
            )
            viewModel.onEvent(BrowseFeedsScreenEvent.OnSearchQueryChanged("  Compose  "))
            advanceTimeBy(300)
            runCurrent()
            assertEquals(1, fixture.repository.requests.size)

            viewModel.onEvent(BrowseFeedsScreenEvent.OnSearchQueryChanged("Co"))
            runCurrent()
            assertEquals(emptySet(), fixture.repository.activeQueries)
            assertEquals(listOf<FeedDiscoveryQuery>(FeedDiscoveryQuery.Search("Compose")), fixture.repository.cancelledQueries)
            assertEquals(1, fixture.repository.requests.size)
        } finally {
            viewModel.viewModelScope.cancel()
            stopKoin()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `Given highlighted discovery metadata When mapped Then text spans icons initials and import state are preserved`() = runTest {
        val fixture = Fixture(UnconfinedTestDispatcher(testScheduler))
        fixture.repository.feedState(FeedId).value = feed().copy(search = FeedDiscoverySearchHighlights(
                highlightedTitle = "\uE000Kotlin\uE001 Weekly",
                highlightedDescription = "A \uE000weekly\uE001 feed",
                highlightedUrl = null,
                highlightedWebsiteUrl = null,
            ))
        val result = requireNotNull(fixture.interactor.observeSelectedFeed(flowOf(FeedId)).first().second)
        assertEquals("Kotlin Weekly", result.title)
        assertEquals("KW", result.initials)
        assertEquals("https://example.com/rss", result.feedUrl)
        assertEquals("https://www.google.com/s2/favicons?domain=example.com&sz=128", result.imageUrl)
        assertEquals(SimpleTextResource("A weekly feed"), result.description)
        assertEquals(
            AnnotatedStringTextResource("Kotlin Weekly", listOf(TextResourceSpan(start = 0, end = 6, fontWeight = 700))),
            result.searchHighlights?.title,
        )
        assertEquals(
            AnnotatedStringTextResource("A weekly feed", listOf(TextResourceSpan(start = 2, end = 8, fontWeight = 700))),
            result.searchHighlights?.description,
        )
        val states = listOf(
            FeedDiscoveryImportState.Added to UIDiscoveryFeedImportState.Added,
            FeedDiscoveryImportState.Adding to UIDiscoveryFeedImportState.Adding,
            FeedDiscoveryImportState.Queued to UIDiscoveryFeedImportState.Queued,
            FeedDiscoveryImportState.Failed to UIDiscoveryFeedImportState.Failed,
            FeedDiscoveryImportState.NotAdded to UIDiscoveryFeedImportState.NotAdded,
        )
        states.forEach { (domain, ui) ->
            fixture.repository.feedState(FeedId).value = feed().copy(importState = domain)
            val selected = fixture.interactor.observeSelectedFeed(flowOf(FeedId)).first().second
            assertEquals(ui, selected?.importState)
        }
        fixture.repository.feedState(FeedId).value = feed().copy(iconUrl = "https://cdn.example.com/icon.png")
        val explicitIcon = requireNotNull(fixture.interactor.observeSelectedFeed(flowOf(FeedId)).first().second)
        assertEquals("https://cdn.example.com/icon.png", explicitIcon.imageUrl)
        assertNull(explicitIcon.searchHighlights?.title)
        assertNull(explicitIcon.searchHighlights?.description)
        assertNull(explicitIcon.searchHighlights?.feedUrl)
        assertNull(explicitIcon.searchHighlights?.websiteUrl)
    }

    private class Fixture(dispatcher: CoroutineDispatcher) {
        val events = mutableListOf<String>()
        val repository = FakeFeedDiscoveryRepository()
        val queue = FakeQueueRepository(events)
        private val dispatchers = object : DispatcherProvider {
            override val defaultDispatcher = dispatcher
            override val ioDispatcher = dispatcher
        }
        val enqueueImport = EnqueueFeedDiscoveryImportUseCase(
            feedDiscoveryRepository = repository,
            enqueueFeedImportsUseCase = EnqueueFeedImportsUseCase(
                rssFeedQueueRepository = queue,
                dispatcherProvider = dispatchers,
                dateTimeProvider = FixedTime,
                logger = DPLog.tag("DiscoveryTest"),
            ),
            dispatcherProvider = dispatchers,
        )
        val interactor = DiscoveryFeedInteractor(
            observeFeedDiscoveryCollectionShelvesUseCase = ObserveFeedDiscoveryCollectionShelvesUseCase(repository),
            observeFeedDiscoveryCollectionUseCase = ObserveFeedDiscoveryCollectionUseCase(repository),
            observeFeedDiscoveryFeedUseCase = ObserveFeedDiscoveryFeedUseCase(repository),
            getPaginatedFeedDiscoveryFeedsUseCase = GetPaginatedFeedDiscoveryFeedsUseCase(repository),
            extractInitialsUseCase = ExtractInitialsUseCase(),
            dispatcherProvider = dispatchers,
            logger = DPLog.tag("DiscoveryTest"),
        )
    }

    private data class QueryRequest(val query: FeedDiscoveryQuery, val config: PagingConfig)

    private class FakeFeedDiscoveryRepository : FeedDiscoveryRepository {
        private val feeds = mutableMapOf<UUID, MutableStateFlow<FeedDiscoveryFeed?>>()
        val requests = mutableListOf<QueryRequest>()
        val activeQueries = mutableSetOf<FeedDiscoveryQuery>()
        val cancelledQueries = mutableListOf<FeedDiscoveryQuery>()
        fun feedState(id: UUID) = feeds.getOrPut(id) { MutableStateFlow(null) }
        override suspend fun isFeedDiscoveryInitialised() = true
        override suspend fun getDiscoverySeed(seedFilePath: String): DiscoverySeedData = error("Unused")
        override suspend fun initialiseWithSeed(seed: DiscoverySeedData) = Unit
        override suspend fun refresh() = Unit
        override fun observeCollectionShelves(previewCount: Int): Flow<List<FeedDiscoveryCollectionShelf>> = flowOf(emptyList())
        override fun observeCollection(collectionId: UUID): Flow<FeedDiscoveryCollection?> = flowOf(null)
        override fun observeFeed(feedId: UUID): Flow<FeedDiscoveryFeed?> = feedState(feedId)
        override fun observeFeeds(query: FeedDiscoveryQuery, pagingConfig: PagingConfig): Flow<PagingData<FeedDiscoveryFeed>> {
            requests += QueryRequest(query, pagingConfig)
            return flow {
                activeQueries += query
                try {
                    emit(PagingData.from(listOf(feed())))
                    awaitCancellation()
                } finally {
                    activeQueries -= query
                    cancelledQueries += query
                }
            }
        }
    }

    private class FakeQueueRepository(private val events: MutableList<String>) : RssFeedQueueRepository {
        val entries = mutableListOf<RssFeedQueueEntry>()
        var failure: Throwable? = null
        override suspend fun enqueue(entry: RssFeedQueueEntry) = enqueue(listOf(entry))
        override suspend fun enqueue(entries: List<RssFeedQueueEntry>) {
            events += "enqueue"
            failure?.let { throw it }
            this.entries += entries
        }
        override suspend fun updateQueueEntry(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun getNextToProcess(): RssFeedQueueEntry? = null
        override fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>> = flowOf(entries)
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
        override fun observeQueuePagingData(status: Set<RssFeedQueueStatus>): Flow<PagingData<RssFeedQueueEntry>> =
            flowOf(PagingData.empty())
        override fun observeQueue(status: Set<RssFeedQueueStatus>): Flow<List<RssFeedQueueEntry>> = flowOf(entries)
    }

    private object FixedTime : DateTimeProvider {
        override fun now(): Instant = Instant.fromEpochMilliseconds(1234)
        override fun nowEpochMilliseconds(): Long = 1234
        override fun parse(string: String): Instant? = null
        override fun getTimeElapsed(instant: Instant): Duration = now() - instant
        override fun today(): LocalDate = LocalDate(2026, 10, 16)
        override fun timeInFuture(duration: Duration): Long = (now() + duration).toEpochMilliseconds()
    }

    private companion object {
        val FeedId = UUID("discovery-feed")
        fun feed() = FeedDiscoveryFeed(
            id = FeedId,
            title = "Kotlin Weekly",
            description = "A weekly feed",
            url = "https://example.com/rss",
            websiteUrl = "https://www.example.com/articles",
            iconUrl = null,
            importState = FeedDiscoveryImportState.NotAdded,
        )
    }
}
