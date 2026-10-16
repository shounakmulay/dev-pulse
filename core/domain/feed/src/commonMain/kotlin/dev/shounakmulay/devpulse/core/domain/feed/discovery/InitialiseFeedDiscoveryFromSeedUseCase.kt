package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.resources.ResourceFilePath
import org.koin.core.annotation.Factory

@Factory
class InitialiseFeedDiscoveryFromSeedUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
    private val dispatcherProvider: DispatcherProvider,
    logger: DPLogger
) {
    companion object {
        private const val TAG = "InitialiseFeedDiscoveryFromSeedUseCase"
    }

    private val log = logger.withTag(TAG)

    suspend operator fun invoke() = dispatcherProvider.runCatchingOnDefault {
        val isInitialised = feedDiscoveryRepository.isFeedDiscoveryInitialised()

        if (isInitialised) {
            log.d { "Feed discovery already initialised. Skipping..." }
            return@runCatchingOnDefault
        }

        val seed = feedDiscoveryRepository.getDiscoverySeed(ResourceFilePath.DISCOVERY_SEED)
        feedDiscoveryRepository.initialiseWithSeed(seed)
        log.d { "Feed discovery initialised." }
    }
}
