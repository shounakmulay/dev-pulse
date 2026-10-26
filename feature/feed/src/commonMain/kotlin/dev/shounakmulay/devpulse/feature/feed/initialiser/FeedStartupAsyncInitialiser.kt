package dev.shounakmulay.devpulse.feature.feed.initialiser

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.common.startup.StartupPriority
import dev.shounakmulay.devpulse.core.domain.feed.discovery.InitialiseFeedDiscoveryFromSeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedSyncUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.InitialiseFeedQueueProcessingUseCase
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveSyncInBackgroundUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory


@Factory
class FeedStartupAsyncInitialiser(
    private val enqueueFeedSyncUseCase: EnqueueFeedSyncUseCase,
    private val initialiseFeedQueueProcessingUseCase: InitialiseFeedQueueProcessingUseCase,
    private val observeSyncInBackgroundUseCase: ObserveSyncInBackgroundUseCase,
    private val initialiseFeedDiscoveryFromSeedUseCase: InitialiseFeedDiscoveryFromSeedUseCase
) : StartupAsyncInitialiser {
    override val priority: StartupPriority
        get() = StartupPriority.LOW

    override suspend fun initialise(): Unit = coroutineScope {
        listOf(
            async { enqueueFeedSync() },
            async { initialiseFeedDiscoveryFromSeedUseCase() }
        ).awaitAll()
    }

    private suspend fun enqueueFeedSync() {
        observeSyncInBackgroundUseCase().first().onSuccess { enabled ->
            if (!enabled) {
                enqueueFeedSyncUseCase()
                initialiseFeedQueueProcessingUseCase()
            }
        }
    }
}
