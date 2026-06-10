package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetFeedDetailUseCase(
    private val feedRepository: ContentFeedRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(id: String): Flow<Result<RssFeed>> {
        return feedRepository.getFeed(id)
            .flowCachingOnDefault(dispatcherProvider)
    }
}