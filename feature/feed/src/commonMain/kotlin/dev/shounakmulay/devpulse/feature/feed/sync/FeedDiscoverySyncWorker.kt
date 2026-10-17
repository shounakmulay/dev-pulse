package dev.shounakmulay.devpulse.feature.feed.sync

import dev.shounakmulay.devpulse.core.domain.feed.discovery.RefreshFeedDiscoveryUseCase
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.sync.DevPulseWorker
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import dev.shounakmulay.devpulse.core.sync.toWorkerResult
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
@Named(DevPulseWorkerType.FEED_DISCOVERY_SYNC)
class FeedDiscoverySyncWorker(
    private val refreshFeedDiscoveryUseCase: RefreshFeedDiscoveryUseCase,
    logger: DPLogger,
) : DevPulseWorker {
    private companion object {
        const val TAG = "FeedDiscoverySyncWorker"
    }

    private val log = logger.withTag(TAG)

    override val identifier: String = DevPulseWorkerType.FEED_DISCOVERY_SYNC

    override suspend fun doWork(input: Map<String, Any?>): Result<Map<String, Any?>?> {
        log.i { "Discovery sync worker started" }
        return refreshFeedDiscoveryUseCase()
            .onSuccess { log.i { "Discovery sync worker completed" } }
            .onFailure { log.w { "Discovery sync worker returning failure" } }
            .toWorkerResult()
    }
}