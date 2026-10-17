package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.logging.DPLogger
import org.koin.core.annotation.Factory

@Factory
class RefreshFeedDiscoveryUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
    private val dispatcherProvider: DispatcherProvider,
    logger: DPLogger,
) {
    private companion object {
        const val TAG = "RefreshFeedDiscoveryUseCase"
    }

    private val log = logger.withTag(TAG)

    suspend operator fun invoke() = dispatcherProvider.runCatchingOnDefault {
        feedDiscoveryRepository.refresh()
    }.onFailure {
        log.e(it) { "Discovery catalog refresh failed" }
    }
}