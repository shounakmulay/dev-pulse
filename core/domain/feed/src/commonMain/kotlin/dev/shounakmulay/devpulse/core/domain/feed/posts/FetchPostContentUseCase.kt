package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.events.DPEvents
import org.koin.core.annotation.Factory

@Factory
class FetchPostContentUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val dpEvent: DPEvent,
) {
    suspend operator fun invoke(
        postId: UUID,
        type: RssFeedPostContentType = RssFeedPostContentType.MARKDOWN,
    ) = dispatcherProvider.runCatchingOnDefault {
        postRepository.fetchPostContentUseCase(postId, type)
    }.onSuccess {
        dpEvent.track(DPEvents.POST_CONTENT_FETCH_COMPLETED)
    }.onFailure {
        dpEvent.track(DPEvents.POST_CONTENT_FETCH_FAILED)
    }
}
