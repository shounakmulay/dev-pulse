package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedDiscoveryCollectionUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
) {
    operator fun invoke(collectionId: UUID) =
        feedDiscoveryRepository.observeCollection(collectionId)
}
