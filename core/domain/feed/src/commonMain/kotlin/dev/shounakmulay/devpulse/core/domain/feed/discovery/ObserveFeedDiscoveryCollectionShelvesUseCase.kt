package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedDiscoveryCollectionShelvesUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
) {
    operator fun invoke(previewCount: Int = 10) =
        feedDiscoveryRepository.observeCollectionShelves(previewCount)
}
