package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithUserState
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPinnedAndRecentFeedsUseCase(
    private val feedRepository: FeedRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(): Flow<Result<List<RssFeedWithUserState>>> = feedRepository.getPinnedAndRecentFeeds(20)
        .flowCachingOnDefault(dispatcherProvider)
}
