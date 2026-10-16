package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import org.koin.core.annotation.Factory

@Factory
class RefreshFeedDiscoveryUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke() = dispatcherProvider.runCatchingOnDefault {
        feedDiscoveryRepository.refresh()
    }
}