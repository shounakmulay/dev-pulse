package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedDiscoveryFeedUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
) {
    operator fun invoke(feedId: UUID) =
        feedDiscoveryRepository.observeFeed(feedId)
}
