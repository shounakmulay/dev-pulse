package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.common.coroutines.runCachingOnIO
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import org.koin.core.annotation.Factory

@Factory
class SetPostReadUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(id: UUID, isRead: Boolean): Result<Unit> =
        dispatcherProvider.runCachingOnIO { postRepository.setPostRead(id = id, isRead = isRead) }
}
