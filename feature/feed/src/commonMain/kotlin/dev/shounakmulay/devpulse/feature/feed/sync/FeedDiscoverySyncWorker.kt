package dev.shounakmulay.devpulse.feature.feed.sync

import dev.shounakmulay.devpulse.core.domain.feed.discovery.RefreshFeedDiscoveryUseCase
import dev.shounakmulay.devpulse.core.sync.DevPulseWorker
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import dev.shounakmulay.devpulse.core.sync.toWorkerResult
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
@Named(DevPulseWorkerType.FEED_DISCOVERY_SYNC)
class FeedDiscoverySyncWorker(
    private val refreshFeedDiscoveryUseCase: RefreshFeedDiscoveryUseCase
) : DevPulseWorker {
    override val identifier: String = DevPulseWorkerType.FEED_DISCOVERY_SYNC

    override suspend fun doWork(input: Map<String, Any?>): Result<Map<String, Any?>?> {
        return refreshFeedDiscoveryUseCase().toWorkerResult()
    }
}