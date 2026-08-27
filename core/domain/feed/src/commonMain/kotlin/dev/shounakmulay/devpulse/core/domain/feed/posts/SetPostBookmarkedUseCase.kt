package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import org.koin.core.annotation.Factory

@Factory
class SetPostBookmarkedUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(id: UUID, bookmarked: Boolean) =
        dispatcherProvider.runCatchingOnDefault {
            postRepository.setPostBookmarked(id = id, bookmarked = bookmarked)
        }
}
