package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetFeedDetailUseCase(
    private val feedRepository: FeedRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(id: UUID): Flow<Result<RssFeed>> {
        return feedRepository.getFeed(id)
            .flowCachingOnDefault(dispatcherProvider)
    }
}