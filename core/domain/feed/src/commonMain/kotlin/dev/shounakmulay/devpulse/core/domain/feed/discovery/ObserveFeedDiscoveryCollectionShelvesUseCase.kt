package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedDiscoveryCollectionShelvesUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(previewCount: Int = 10) = feedDiscoveryRepository.observeCollectionShelves(previewCount)
        .flowCachingOnDefault(dispatcherProvider)
}
