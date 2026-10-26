package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.domain.feed.queue.ObserveFeedQueueUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.notifications.AndroidNotificationBuilder
import dev.shounakmulay.devpulse.core.notifications.AndroidNotificationProvider
import dev.shounakmulay.devpulse.core.notifications.DPNotificationChannel
import dev.shounakmulay.devpulse.core.notifications.DPNotificationContent
import dev.shounakmulay.devpulse.core.notifications.DPNotificationIdentifier
import dev.shounakmulay.devpulse.core.notifications.DPNotificationProgress
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.service.CoroutineService
import devpulse.core.resources.generated.resources.feed_import_notification_initial_text
import devpulse.core.resources.generated.resources.feed_import_notification_text
import devpulse.core.resources.generated.resources.feed_import_notification_title
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.annotation.Factory
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.compose.resources.getString as getKmpString

@Factory
class FeedImportBackgroundProcessLauncherImpl(
    private val context: Context,
) : FeedImportBackgroundProcessLauncher {
    override suspend fun launch() {
        val intent = Intent(context, FeedImportForegroundService::class.java)
        context.startService(intent)
    }
}

class FeedImportForegroundService : CoroutineService() {

    companion object {
        private const val SERVICE_ID = 100
    }

    private val queueExecutor: RssFeedQueueExecutor by inject()
    private val observeFeedQueue: ObserveFeedQueueUseCase by inject()
    private val androidNotificationBuilder: AndroidNotificationBuilder by inject()
    private val androidNotificationProvider: AndroidNotificationProvider by inject()

    private val dispatcherProvider: DispatcherProvider
        get() = get()

    override val dispatcher: CoroutineDispatcher
        get() = dispatcherProvider.defaultDispatcher

    private val processingMutex = Mutex()

    @Volatile private var latestStartId = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        latestStartId = startId
        if (!processingMutex.tryLock()) {
            queueExecutor.triggerQueueProcessing()
            return START_STICKY
        }
        startProcessing(startId)
        return START_STICKY
    }

    @OptIn(FlowPreview::class)
    private fun startProcessing(startId: Int) {
        serviceScope.launch {
            var finishingStartId = startId
            try {
                val notification = createServiceNotification(
                    text = getKmpString(stringRes.feed_import_notification_initial_text),
                )
                val nativeNotification = androidNotificationBuilder.createNotification(notification)
                startForeground(SERVICE_ID, nativeNotification)
                queueExecutor.triggerQueueProcessing()

                val statuses = arrayOf(RssFeedQueueStatus.PROCESSING, RssFeedQueueStatus.QUEUED)
                observeFeedQueue(*statuses)
                    .map { it.getOrThrow() }
                    .onEach {
                        val notification = createServiceNotification(
                            text = getKmpString(stringRes.feed_import_notification_text, it.size),
                        )
                        androidNotificationProvider.showNotification(notification)
                    }
                    .debounce(1.seconds)
                    .first { pending ->
                        if (pending.isNotEmpty()) {
                            false
                        } else {
                            finishingStartId = latestStartId
                            observeFeedQueue(*statuses).first().getOrThrow().isEmpty()
                        }
                    }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                finishingStartId = startId
                DPLog.e(tag = "FeedImportForegroundService", throwable = failure) {
                    "Feed import service failed startId=$startId"
                }
            } finally {
                withContext(NonCancellable + dispatcherProvider.mainDispatcher.immediate) {
                    processingMutex.unlock()
                    if (serviceScope.isActive) {
                        if (stopSelfResult(finishingStartId)) {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                        } else if (processingMutex.tryLock()) {
                            startProcessing(latestStartId)
                        }
                    }
                }
            }
        }
    }

    private suspend fun createServiceNotification(
        text: String,
    ): DPNotificationContent = DPNotificationContent(
        identifier = DPNotificationIdentifier.FEED_IMPORT_SERVICE,
        channel = DPNotificationChannel.FEED_IMPORT,
        progress = DPNotificationProgress.Indeterminate,
        title = getKmpString(stringRes.feed_import_notification_title),
        text = text,
    )

    override fun onBind(p0: Intent?): IBinder? = null
}
