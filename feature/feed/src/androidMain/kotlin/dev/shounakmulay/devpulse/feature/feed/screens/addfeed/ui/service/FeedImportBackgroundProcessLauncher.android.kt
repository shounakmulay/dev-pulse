package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.shounakmulay.devpulse.core.domain.feed.queue.ObserveFeedQueueUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
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

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.Default


    private val processingMutex = Mutex()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!processingMutex.tryLock()) {
            return START_STICKY
        }

        serviceScope.launch {
            val notification = createServiceNotification(
                text = getKmpString(stringRes.feed_import_notification_initial_text)
            )
            val nativeNotification = androidNotificationBuilder.createNotification(notification)

            startForeground(SERVICE_ID, nativeNotification)

            observeFeedQueue(
                setOf(
                    RssFeedQueueStatus.PROCESSING,
                    RssFeedQueueStatus.QUEUED
                )
            )
                .onEach {
                    val notification = createServiceNotification(
                        text = getKmpString(stringRes.feed_import_notification_text, it.size)
                    )
                    androidNotificationProvider.showNotification(notification)
                }
                .debounce(1.seconds)
                .filter { it.isEmpty() }
                .onEach {
                    processingMutex.unlock()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
                .launchIn(serviceScope)

            queueExecutor.triggerQueueProcessing()
        }

        return START_STICKY
    }

    private suspend fun createServiceNotification(
        text: String
    ): DPNotificationContent = DPNotificationContent(
        identifier = DPNotificationIdentifier.FEED_IMPORT_SERVICE,
        channel = DPNotificationChannel.FEED_IMPORT,
        progress = DPNotificationProgress.Indeterminate,
        title = getKmpString(stringRes.feed_import_notification_title),
        text = text,
    )

    override fun onBind(p0: Intent?): IBinder? = null

}