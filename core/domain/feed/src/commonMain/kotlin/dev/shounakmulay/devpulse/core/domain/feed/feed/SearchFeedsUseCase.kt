package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import org.koin.core.annotation.Factory

@Factory
class SearchFeedsUseCase(
    private val feedRepository: FeedRepository,
) {
    operator fun invoke(query: String) = feedRepository.searchFeeds(query, snippetLength = 100)
}
