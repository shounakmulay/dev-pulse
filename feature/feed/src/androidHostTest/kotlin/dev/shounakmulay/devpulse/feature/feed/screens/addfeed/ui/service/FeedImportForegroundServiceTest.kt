package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.service

import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.Context
import android.content.ContextWrapper
import android.app.NotificationManager
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.feed_import_notification_initial_text
import devpulse.core.resources.generated.resources.feed_import_notification_text
import devpulse.core.resources.generated.resources.feed_import_notification_title
import org.jetbrains.compose.resources.getString
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.feed.queue.ObserveFeedQueueUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.*
import dev.shounakmulay.devpulse.core.logging.log.LogEntry
import dev.shounakmulay.devpulse.core.logging.log.LogSink
import kotlinx.coroutines.launch
import dev.shounakmulay.devpulse.core.logging.log.DPLogConfig
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.notifications.AndroidNotificationBuilder
import dev.shounakmulay.devpulse.core.notifications.AndroidNotificationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implements
import org.robolectric.annotation.Implementation
import org.robolectric.shadows.ShadowService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [35], shadows = [FeedImportForegroundServiceTest.StartAwareServiceShadow::class])
class FeedImportForegroundServiceTest {
    @kotlin.test.BeforeTest
    fun configureLogging() {
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Assert))
    }

    @Test
    fun `Given completed or failed import When another starts during idle delay Then it is processed`() = runTest {
        for (terminal in listOf(RssFeedQueueStatus.COMPLETED, RssFeedQueueStatus.FAILED)) {
            withFixture { fixture ->
                fixture.queue.pending.value = listOf(entry(1))
                fixture.start(1)
                runCurrent()
                fixture.queue.pending.value = listOf(entry(1, terminal))
                advanceTimeBy(100)
                fixture.queue.pending.value = listOf(entry(2))
                fixture.start(2)
                runCurrent()
                assertTrue(2 in fixture.executor.processed, "Second import after $terminal must receive a wake-up")
                fixture.queue.pending.value = emptyList()
                advanceTimeBy(1_001)
                runCurrent()
                assertTrue(shadowOf(fixture.service).isStoppedBySelf)
                assertTrue(shadowOf(fixture.service).isForegroundStopped)
                assertEquals(2, shadowOf(fixture.service).stopSelfResultId)
            }
        }
    }

    @Test
    fun `Given active service When a new import starts Then new entries are processed`() = runTest {
        withFixture { fixture ->
            fixture.start(1)
            runCurrent()
            advanceTimeBy(100)
            fixture.queue.pending.value = listOf(entry(2))
            fixture.start(2)
            runCurrent()
            assertEquals(listOf(2), fixture.executor.processed)
            advanceTimeBy(1_001)
            runCurrent()
            assertFalse(shadowOf(fixture.service).isStoppedBySelf)
        }
    }

    @Test
    fun `Given idle shutdown When a newer start arrives Then old subscription cannot stop it`() = runTest {
        withFixture { fixture ->
            fixture.start(1)
            runCurrent()
            advanceTimeBy(900)
            fixture.queue.pending.value = listOf(entry(2))
            fixture.start(2)
            runCurrent()
            advanceTimeBy(200)
            runCurrent()
            assertFalse(shadowOf(fixture.service).isStoppedBySelf)
            fixture.queue.pending.value = emptyList()
            advanceTimeBy(1_001)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
        }
    }

    @Test
    fun `Given failed observation When service starts again Then a new subscription processes persisted entries`() = runTest {
        withFixture { fixture ->
            fixture.queue.failObservation = true
            fixture.start(1)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
            fixture.queue.failObservation = false
            fixture.queue.pending.value = listOf(entry(3, RssFeedQueueStatus.PROCESSING))
            fixture.start(2, null)
            runCurrent()
            assertTrue(3 in fixture.executor.processed)
            assertEquals(2, fixture.queue.subscriptions)
        }
    }

    @Test
    fun `Given notification update fails When another start arrives Then service can observe and process again`() = runTest {
        withFixture { fixture ->
            fixture.notificationContext.fail = true
            fixture.queue.pending.value = listOf(entry(1))
            fixture.start(1)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
            fixture.notificationContext.fail = false
            fixture.queue.pending.value = listOf(entry(2))
            fixture.start(2)
            runCurrent()
            assertTrue(2 in fixture.executor.processed)
            assertEquals(2, fixture.queue.subscriptions)
        }
    }

    @Test
    fun `Given an active observer When starts repeat Then one subscription keeps processing queue changes`() = runTest {
        withFixture { fixture ->
            fixture.queue.pending.value = listOf(entry(1))
            fixture.start(1)
            runCurrent()
            fixture.start(2)
            fixture.start(3)
            runCurrent()
            assertEquals(1, fixture.queue.subscriptions)
            fixture.queue.pending.value = listOf(entry(2))
            fixture.start(4)
            runCurrent()
            assertEquals(listOf(1, 2), fixture.executor.processed)
            assertEquals(1, fixture.queue.subscriptions)
        }
    }

    @Test
    fun `Given an empty queue When another start arrives Then shutdown uses the latest start ID`() = runTest {
        withFixture { fixture ->
            fixture.start(1)
            runCurrent()
            advanceTimeBy(900)
            fixture.start(2)
            runCurrent()
            assertEquals(1, fixture.queue.subscriptions)
            advanceTimeBy(200)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
            assertEquals(2, shadowOf(fixture.service).stopSelfResultId)
        }
    }

    @Test
    fun `Given a stale empty observation When persisted imports are pending Then shutdown rechecks and keeps the service alive`() = runTest {
        withFixture { fixture ->
            fixture.start(1)
            runCurrent()
            advanceTimeBy(900)
            fixture.queue.freshPending = listOf(entry(2))
            fixture.start(2)
            runCurrent()
            advanceTimeBy(1_001)
            runCurrent()
            assertFalse(shadowOf(fixture.service).isStoppedBySelf)
            fixture.queue.freshPending = null
            fixture.queue.pending.value = listOf(entry(2))
            runCurrent()
            assertTrue(2 in fixture.executor.processed)
            fixture.queue.pending.value = emptyList()
            advanceTimeBy(1_001)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
            assertEquals(2, shadowOf(fixture.service).stopSelfResultId)
        }
    }

    @Test
    fun `Given an observer is failing When another start arrives before cleanup Then the new start recovers processing`() = runTest {
        withFixture { fixture ->
            fixture.queue.failObservation = true
            var newStartQueued = false
            val sink = object : LogSink {
                override fun isLoggable(entry: LogEntry) = true
                override fun log(logEntry: LogEntry) {
                    if (!newStartQueued) {
                        newStartQueued = true
                        backgroundScope.launch(Dispatchers.Main.immediate) {
                            fixture.queue.failObservation = false
                            fixture.queue.pending.value = listOf(entry(2))
                            fixture.start(2)
                        }
                    }
                }
            }
            DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Error, sinks = listOf(sink)))
            fixture.start(1)
            runCurrent()
            assertTrue(2 in fixture.executor.processed)
            assertEquals(2, fixture.queue.subscriptions)
            fixture.queue.pending.value = emptyList()
            advanceTimeBy(1_001)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
            assertEquals(2, shadowOf(fixture.service).stopSelfResultId)
        }
    }

    @Test
    fun `Given foreground promotion fails When service starts again Then processing can recover`() = runTest {
        withFixture { fixture ->
            shadowOf(fixture.service).setThrowInStartForeground(SecurityException("permission missing"))
            fixture.start(1)
            runCurrent()
            assertTrue(shadowOf(fixture.service).isStoppedBySelf)
            shadowOf(fixture.service).setThrowInStartForeground(null)
            fixture.queue.pending.value = listOf(entry(2))
            fixture.start(2)
            runCurrent()
            assertTrue(2 in fixture.executor.processed)
        }
    }

    @Test
    fun `Given launcher When import begins Then Android receives a foreground service request`() = runTest {
        val context = RuntimeEnvironment.getApplication() as Application
        FeedImportBackgroundProcessLauncherImpl(context).launch()
        assertEquals(FeedImportForegroundService::class.java.name, shadowOf(context).nextStartedService.component?.className)
    }

    private suspend fun TestScope.withFixture(block: suspend TestScope.(Fixture) -> Unit) {
        val dispatcher = StandardTestDispatcher(testScheduler, name = "service-default")
        Dispatchers.setMain(StandardTestDispatcher(testScheduler, name = "service-main"))
        val queue = Queue()
        val executor = Executor(queue)
        val context = RuntimeEnvironment.getApplication() as Application
        var defaultDispatcherRequested = false
        val dispatchers = object : DispatcherProvider {
            override val defaultDispatcher: kotlinx.coroutines.CoroutineDispatcher
                get() {
                    defaultDispatcherRequested = true
                    return dispatcher
                }
            override val ioDispatcher = dispatcher
        }
        val notificationContext = NotificationContext(context)
        startKoin {
            modules(module {
                single<DispatcherProvider> { dispatchers }
                single<RssFeedQueueExecutor> { executor }
                single { ObserveFeedQueueUseCase(queue, dispatchers) }
                single { AndroidNotificationBuilder(context) }
                single { AndroidNotificationProvider(notificationContext, get()) }
                single { DPLog.tag("FeedImportForegroundServiceTest") }
            })
        }
        getString(stringRes.feed_import_notification_initial_text)
        getString(stringRes.feed_import_notification_text, 0)
        getString(stringRes.feed_import_notification_title)
        val controller = Robolectric.buildService(FeedImportForegroundService::class.java).create()
        try {
            assertTrue(defaultDispatcherRequested, "Service scope must use the injected Default dispatcher")
            block(Fixture(controller.get(), queue, executor, notificationContext))
        } finally {
            controller.destroy()
            runCurrent()
            stopKoin()
            Dispatchers.resetMain()
        }
    }

    @Implements(Service::class)
    class StartAwareServiceShadow : ShadowService() {
        var latestStartId = 0

        @Implementation
        public override fun stopSelfResult(startId: Int): Boolean {
            if (startId != latestStartId) return false
            return super.stopSelfResult(startId)
        }
    }

    private class NotificationContext(context: Context) : ContextWrapper(context) {
        var fail = false
        override fun getSystemService(name: String): NotificationManager {
            check(!fail) { "notification manager unavailable" }
            return baseContext.getSystemService(NotificationManager::class.java)
        }
    }

    private class Fixture(val service: FeedImportForegroundService, val queue: Queue, val executor: Executor, val notificationContext: NotificationContext) {
        fun start(id: Int, intent: Intent? = Intent()): Int {
            (shadowOf(service) as StartAwareServiceShadow).latestStartId = id
            return service.onStartCommand(intent, 0, id)
        }
    }

    private class Executor(private val queue: Queue) : RssFeedQueueExecutor {
        val processed = mutableListOf<Int>()
        override fun triggerQueueProcessing() {
            (queue.freshPending ?: queue.pending.value).forEach { if (it.id !in processed) processed += it.id }
        }
        override suspend fun process(): Set<UUID> = error("Unused")
        override fun isProcessing() = false
    }

    private class Queue : RssFeedQueueRepository {
        val pending = MutableStateFlow<List<RssFeedQueueEntry>>(emptyList())
        var failObservation = false
        var freshPending: List<RssFeedQueueEntry>? = null
        var subscriptions = 0
        override fun observeQueue(status: Set<RssFeedQueueStatus>): Flow<List<RssFeedQueueEntry>> {
            subscriptions++
            if (failObservation) return flow { error("database unavailable") }
            val fresh = freshPending
            return if (subscriptions > 1 && fresh != null) flowOf(fresh.filter { it.status in status })
            else pending.map { entries -> entries.filter { it.status in status } }
        }
        override suspend fun enqueue(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun enqueue(entries: List<RssFeedQueueEntry>) = error("Unused")
        override suspend fun updateQueueEntry(entry: RssFeedQueueEntry) = error("Unused")
        override suspend fun getNextToProcess(): RssFeedQueueEntry? = error("Unused")
        override fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>> = error("Unused")
        override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) = error("Unused")
        override fun observeQueuePagingData(status: Set<RssFeedQueueStatus>): Flow<PagingData<RssFeedQueueEntry>> = error("Unused")
    }

    private fun entry(id: Int, status: RssFeedQueueStatus = RssFeedQueueStatus.QUEUED) = RssFeedQueueEntry(
        id = id, feedId = null, url = "https://example.com/$id", name = null,
        feedType = RssFeedType.CONTENT, actionType = RssFeedQueueActionType.IMPORT,
        requestor = RssFeedQueueActionRequestor.USER, status = status, createdAt = 0, updatedAt = 0
    )
}
