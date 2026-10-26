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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.compose.resources.getString as getKmpString

@Factory
class FeedImportBackgroundProcessLauncherImpl(
    private val context: Context,
) : FeedImportBackgroundProcessLauncher {
    override suspend fun launch() {
        val intent = Intent(context, FeedImportForegroundService::class.java)
        context.startForegroundService(intent)
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

    private var queueObserver: Job? = null
    private val logger = DPLog.tag("FeedImportForegroundService")

    @OptIn(FlowPreview::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        queueObserver?.cancel()
        queueObserver = serviceScope.launch {
            try {
                val notification = createServiceNotification(
                    text = getKmpString(stringRes.feed_import_notification_initial_text),
                )
                val nativeNotification = androidNotificationBuilder.createNotification(notification)
                withContext(dispatcherProvider.mainDispatcher.immediate) {
                    startForeground(SERVICE_ID, nativeNotification)
                }
                queueExecutor.triggerQueueProcessing()

                observeFeedQueue(
                    RssFeedQueueStatus.PROCESSING,
                    RssFeedQueueStatus.QUEUED,
                )
                    .map { it.getOrThrow() }
                    .distinctUntilChanged()
                    .onEach { pending ->
                        if (pending.isNotEmpty()) queueExecutor.triggerQueueProcessing()
                        val notification = createServiceNotification(
                            text = getKmpString(stringRes.feed_import_notification_text, pending.size),
                        )
                        withContext(dispatcherProvider.mainDispatcher.immediate) {
                            androidNotificationProvider.showNotification(notification)
                        }
                    }
                    .debounce(1.seconds)
                    .first { it.isEmpty() }
                stopSession(startId)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                logger.e(failure) { "Feed import service session failed startId=$startId" }
                stopSession(startId)
            }
        }
        return START_STICKY
    }

    private suspend fun stopSession(startId: Int) = withContext(dispatcherProvider.mainDispatcher.immediate) {
        if (stopSelfResult(startId)) {
            stopForeground(STOP_FOREGROUND_REMOVE)
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
