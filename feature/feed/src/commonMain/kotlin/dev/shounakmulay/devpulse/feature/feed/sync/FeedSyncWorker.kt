package dev.shounakmulay.devpulse.feature.feed.sync

import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedSyncUseCase
import dev.shounakmulay.devpulse.core.sync.DevPulseWorker
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
@Named("FEED_SYNC")
class FeedSyncWorker(
    private val enqueueFeedSyncUseCase: EnqueueFeedSyncUseCase
) : DevPulseWorker {
    override val identifier: DevPulseWorkerType = DevPulseWorkerType.FEED_SYNC

    override suspend fun doWork(input: Map<String, Any?>): Result<Map<String, Any?>?> {
        enqueueFeedSyncUseCase()
        return Result.success(null)
    }
}
