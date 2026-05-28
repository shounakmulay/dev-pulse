package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.common.extensions.mapToSuccessNotNull
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetRecentFeedItemsUseCase(
    private val feedRepository: ContentFeedRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(): Flow<List<RssPostWithFeedIdentity>> {
        return feedRepository.getRecentPosts(30)
            .flowCachingOnDefault(dispatcherProvider)
            .mapToSuccessNotNull()
    }
}
