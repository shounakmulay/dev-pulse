package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentity
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPostDetailUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(id: UUID): Flow<Result<RssPostWithFeedIdentity>> {
        return postRepository.getPost(id)
            .flowCachingOnDefault(dispatcherProvider)
    }
}
