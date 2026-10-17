package dev.shounakmulay.devpulse.core.domain.feed.discovery

import androidx.paging.PagingConfig
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import org.koin.core.annotation.Factory

@Factory
class GetPaginatedFeedDiscoveryFeedsUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
) {
    operator fun invoke(query: FeedDiscoveryQuery) = feedDiscoveryRepository.observeFeeds(
        query = query,
        pagingConfig = PagingConfig(
            pageSize = 20,
            initialLoadSize = 40,
            prefetchDistance = 5,
            enablePlaceholders = false,
        ),
    )
}
