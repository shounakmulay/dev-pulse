package dev.shounakmulay.devpulse.feature.feed.sync

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.time.DefaultDateTimeProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedSyncRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.data.settings.FeedSettingsRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedSyncUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueProcessingResult
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.*
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveSyncInBackgroundUseCase
import dev.shounakmulay.devpulse.core.notifications.DPNotificationContent
import dev.shounakmulay.devpulse.core.notifications.DPNotificationIdentifier
import dev.shounakmulay.devpulse.core.notifications.NotificationProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [35])
class FeedSyncWorkerTest {
    @Test
    fun `Given empty or unchanged feeds When sync completes Then no notification is shown`() = runTest {
        for (feeds in listOf(emptySet(), setOf(UUID("unchanged")))) {
            val fixture = Fixture(StandardTestDispatcher(testScheduler))
            fixture.executor.result = RssFeedQueueProcessingResult(syncedFeedIds = feeds)
            assertTrue(fixture.worker.doWork(emptyMap()).isSuccess)
            assertEquals(emptyList(), fixture.notifications.shown)
            assertTrue(fixture.executor.processed)
        }
    }

    @Test
    fun `Given new articles in one of two feeds When sync completes Then one feed is announced`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.executor.result = RssFeedQueueProcessingResult(
            syncedFeedIds = setOf(UUID("new"), UUID("unchanged")),
            feedsWithNewPosts = setOf(UUID("new")),
        )
        assertTrue(fixture.worker.doWork(emptyMap()).isSuccess)
        val notification = fixture.notifications.shown.single()
        assertEquals(DPNotificationIdentifier.FEED_SYNC_WORKER, notification.identifier)
        assertEquals("1 feeds synced successfully", notification.text)
    }

    @Test
    fun `Given background sync disabled When worker runs Then no processing or notification occurs`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.settings.enabled = false
        assertTrue(fixture.worker.doWork(emptyMap()).isSuccess)
        assertEquals(false, fixture.executor.processed)
        assertEquals(emptyList(), fixture.notifications.shown)
    }

    @Test
    fun `Given notification permission denied When sync completes Then worker still succeeds`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.executor.result = RssFeedQueueProcessingResult(feedsWithNewPosts = setOf(UUID("new")))
        fixture.notifications.failure = SecurityException("notification denied")
        assertTrue(fixture.worker.doWork(emptyMap()).isSuccess)
    }

    @Test
    fun `Given cancellation When processing Then cancellation propagates without notification`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.executor.failure = CancellationException("worker stopped")
        assertFailsWith<CancellationException> { fixture.worker.doWork(emptyMap()) }
        assertEquals(emptyList(), fixture.notifications.shown)
    }

    private class Fixture(dispatcher: kotlinx.coroutines.CoroutineDispatcher) {
        private val dispatchers = object : DispatcherProvider {
            override val defaultDispatcher = dispatcher
            override val ioDispatcher = dispatcher
        }
        val settings = Settings()
        val executor = Executor()
        val notifications = Notifications()
        val worker = FeedSyncWorker(
            enqueueFeedSyncUseCase = EnqueueFeedSyncUseCase(
                rssFeedQueueRepository = Queue(),
                feedSyncRepository = Sync(),
                dispatcherProvider = dispatchers,
                dateTimeProvider = DefaultDateTimeProvider(),
            ),
            queueExecutor = executor,
            observeSyncInBackgroundUseCase = ObserveSyncInBackgroundUseCase(settings, dispatchers),
            notificationProvider = notifications,
            dispatcherProvider = dispatchers,
        )
    }

    private class Executor : RssFeedQueueExecutor {
        var result = RssFeedQueueProcessingResult()
        var processed = false
        var failure: Exception? = null
        override suspend fun process(): RssFeedQueueProcessingResult {
            processed = true
            failure?.let { throw it }
            return result
        }
        override fun triggerQueueProcessing() = error("Unused")
        override fun isProcessing() = false
    }

    private class Notifications : NotificationProvider {
        val shown = mutableListOf<DPNotificationContent>()
        var failure: Exception? = null
        override suspend fun initialise() = error("Unused")
        override suspend fun showNotification(notification: DPNotificationContent) {
            failure?.let { throw it }
            shown += notification
        }
    }

    private class Settings : FeedSettingsRepository {
        var enabled = true
        override fun observeSyncInBackground(defaultValue: Boolean) = flowOf(enabled)
        override suspend fun setSyncInBackground(enabled: Boolean) = error("Unused")
        override fun observeFeedPostListItemVariant(defaultVariant: FeedsPostListItemVariant): Flow<FeedsPostListItemVariant> = error("Unused")
        override suspend fun setFeedPostListItemVariant(variant: FeedsPostListItemVariant) = error("Unused")
    }

    private class Queue : RssFeedQueueRepository {
        override suspend fun enqueue(entries: List<RssFeedQueueEntry>) = Unit
        override suspend fun enqueue(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun updateQueueEntry(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun getNextToProcess(): RssFeedQueueEntry? = error("Unused")
        override fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>> = error("Unused")
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
        override fun observeQueuePagingData(status: Set<RssFeedQueueStatus>): Flow<PagingData<RssFeedQueueEntry>> = error("Unused")
        override fun observeQueue(status: Set<RssFeedQueueStatus>): Flow<List<RssFeedQueueEntry>> = error("Unused")
    }

    private class Sync : FeedSyncRepository {
        override suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>> = emptyList()
        override suspend fun importPosts(postsWithExistingIdentities: List<RssFeedPostWithExistingIdentity>) = error("Unused")
        override suspend fun importFeed(entry: RssFeedQueueEntry, parsedFeed: ParsedFeed, existingIdentity: RssFeedIdentity?): RssFeed = error("Unused")
        override suspend fun onImportCompleted(entry: RssFeedQueueEntry, feed: RssFeed, savedCount: Int, metadata: RssFeedSyncMetadata, removeStaleEntries: Boolean) = error("Unused")
        override suspend fun onImportFailed(entry: RssFeedQueueEntry, metadata: RssFeedSyncMetadata?) = error("Unused")
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
    }
}
