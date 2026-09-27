package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import org.koin.core.annotation.Factory

@Factory
class SearchFeedsUseCase(
    private val feedRepository: FeedRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(query: String) = dispatcherProvider.runCatchingOnDefault {
        feedRepository.searchFeeds(query, snippetLength = 100)
    }
}