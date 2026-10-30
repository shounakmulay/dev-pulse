package dev.shounakmulay.devpulse.feature.feed.components.feedOptions

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.DeleteFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithUserState
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.navigation.NavigationState
import dev.shounakmulay.devpulse.core.navigation.removeFeedRoutes
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FeedOptionsMenuProcessorTest {
    @Test
    fun `Given feed history When deletion succeeds Then owned routes are pruned before showing success`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))

        fixture.delete()

        assertEquals(listOf(FeedId), fixture.repository.deleted)
        assertEquals(listOf(listOf<Screen>(Screen.Tabs)), fixture.historyAtToast)
        assertEquals(listOf<Screen>(Screen.Tabs), fixture.state.rootStack.toList())
    }

    @Test
    fun `Given feed history When deletion fails Then navigation stays unchanged`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.repository.failure = IllegalStateException("database busy")

        fixture.delete()

        assertEquals(OriginalHistory, fixture.state.rootStack.toList())
        assertEquals(listOf(OriginalHistory), fixture.historyAtToast)
        assertEquals(emptyList(), fixture.repository.deleted)
    }

    @Test
    fun `Given feed history When deletion is cancelled Then navigation stays unchanged and cancellation propagates`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.repository.failure = CancellationException("cancelled")

        assertFailsWith<CancellationException> { fixture.delete() }

        assertEquals(OriginalHistory, fixture.state.rootStack.toList())
        assertEquals(emptyList(), fixture.historyAtToast)
        assertEquals(emptyList(), fixture.repository.deleted)
    }

    private class Fixture(dispatcher: CoroutineDispatcher) {
        val repository = Repository()
        val state = NavigationState(
            rootStack = NavBackStack(*OriginalHistory.toTypedArray()),
            tabsStart = Screen.Tabs.Home,
            selectedTab = mutableStateOf(Screen.Tabs.Feed),
            tabsBackStacks = mapOf(
                Screen.Tabs.Home to NavBackStack(Screen.Tabs.Home),
                Screen.Tabs.Feed to NavBackStack(Screen.Tabs.Feed),
            ),
        )
        val historyAtToast = mutableListOf<List<Screen>>()
        private val navigator = Navigator(state, DPLog.tag("FeedOptionsMenuProcessorTest"))
        private val dispatchers = object : DispatcherProvider {
            override val defaultDispatcher = dispatcher
            override val ioDispatcher = dispatcher
        }
        private val processor = FeedOptionsMenuProcessorDelegate(
            deleteFeedUseCase = DeleteFeedUseCase(repository, dispatchers),
            setPinFeedPinnedUseCase = SetFeedPinnedUseCase(repository),
        )

        suspend fun delete() = processor.processFeedOption(
            feedId = FeedId,
            option = FeedOptionsMenuItem.Delete("Feed A"),
            onShare = { error("Unexpected share") },
            onShowToast = { historyAtToast += state.rootStack.toList() },
            onFeedDeleted = { navigator.removeFeedRoutes(it) },
        )
    }

    private class Repository : FeedRepository {
        var failure: Exception? = null
        val deleted = mutableListOf<UUID>()
        override suspend fun deleteFeed(id: UUID) {
            failure?.let { throw it }
            deleted += id
        }
        override suspend fun upsertFeed(feed: RssFeed) = error("Unused")
        override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
        override fun searchFeeds(query: String, snippetLength: Int): Flow<PagingData<RssFeedSearchResult>> = error("Unused")
        override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeedWithUserState>> = error("Unused")
        override fun getFeed(id: UUID): Flow<RssFeedWithUserState> = error("Unused")
        override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
        override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> = error("Unused")
        override suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData> = error("Unused")
        override suspend fun fetchRssFeed(entry: RssFeedQueueEntry): ParsedFeed = error("Unused")
        override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> = error("Unused")
        override suspend fun getFeedIdentityBySourceUrl(url: String): RssFeedIdentity? = error("Unused")
    }

    private companion object {
        val FeedId = UUID("feed-a")
        val OriginalHistory = listOf(
            Screen.Tabs,
            Screen.Tabs.Feed.FeedDetail(FeedId),
            Screen.Tabs.Feed.PostDetail(id = UUID("article-a"), feedId = FeedId),
            Screen.Tabs.Feed.FeedDetail(FeedId),
        )
    }
}
