package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import org.koin.core.annotation.Factory

@Factory
class SearchFeedPostQueryUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(query: RssPostQuery) = dispatcherProvider.runCatchingOnDefault {
        postRepository.searchPostQuery(query)
    }
}
