package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.common.extensions.mapToSuccessNotNull
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentity
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetRecentFeedItemsUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(): Flow<List<RssPostWithFeedIdentity>> {
        return postRepository.observeRecentPosts(30)
            .flowCachingOnDefault(dispatcherProvider)
            .mapToSuccessNotNull()
    }
}
