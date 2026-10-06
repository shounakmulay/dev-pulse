package dev.shounakmulay.devpulse.feature.feed.sync

import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedSyncUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveSyncInBackgroundUseCase
import dev.shounakmulay.devpulse.core.sync.DevPulseWorker
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
@Named("FEED_SYNC")
class FeedSyncWorker(
    private val enqueueFeedSyncUseCase: EnqueueFeedSyncUseCase,
    private val queueExecutor: RssFeedQueueExecutor,
    private val observeSyncInBackgroundUseCase: ObserveSyncInBackgroundUseCase
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
        queueExecutor.processQueue()
        queueExecutor.awaitProcessing()
        return Result.success(null)
    }
}
