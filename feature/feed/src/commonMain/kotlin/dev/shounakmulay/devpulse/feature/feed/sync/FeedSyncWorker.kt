package dev.shounakmulay.devpulse.feature.feed.sync

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.common.coroutines.safeRunCatching
import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedSyncUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveSyncInBackgroundUseCase
import dev.shounakmulay.devpulse.core.notifications.DPNotificationChannel
import dev.shounakmulay.devpulse.core.notifications.DPNotificationContent
import dev.shounakmulay.devpulse.core.notifications.DPNotificationIdentifier
import dev.shounakmulay.devpulse.core.notifications.NotificationProvider
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.sync.DevPulseWorker
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import dev.shounakmulay.devpulse.core.sync.toWorkerResult
import devpulse.core.resources.generated.resources.feeds_sync_message
import devpulse.core.resources.generated.resources.feeds_sync_title
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
@Named("FEED_SYNC")
class FeedSyncWorker(
    private val enqueueFeedSyncUseCase: EnqueueFeedSyncUseCase,
    private val queueExecutor: RssFeedQueueExecutor,
    private val observeSyncInBackgroundUseCase: ObserveSyncInBackgroundUseCase,
    private val notificationProvider: NotificationProvider,
    private val dispatcherProvider: DispatcherProvider
) : DevPulseWorker {
    override val identifier: DevPulseWorkerType = DevPulseWorkerType.FEED_SYNC

    override suspend fun doWork(input: Map<String, Any?>): Result<Map<String, Any?>?> {
        val syncInBackground = observeSyncInBackgroundUseCase().first().getOrElse {
            return Result.failure(it)
        }
        if (!syncInBackground) return Result.success(null)

        enqueueFeedSyncUseCase().getOrElse {
            return Result.failure(it)
        }

        return dispatcherProvider
            .runCatchingOnDefault {
                val syncedFeeds = queueExecutor.process()
                safeRunCatching {
                    val notification = DPNotificationContent(
                        identifier = DPNotificationIdentifier.FEED_SYNC_WORKER,
                        channel = DPNotificationChannel.FEED_DATA_SYNC,
                        title = getString(stringRes.feeds_sync_title),
                        text = getString(stringRes.feeds_sync_message, syncedFeeds.size)
                    )
                    notificationProvider.showNotification(notification)
                }
            }
            .toWorkerResult()

    }
}
