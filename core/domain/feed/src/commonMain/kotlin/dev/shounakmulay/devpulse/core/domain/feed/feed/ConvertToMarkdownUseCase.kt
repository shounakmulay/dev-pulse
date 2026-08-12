package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import org.koin.core.annotation.Factory

@Factory
class ConvertToMarkdownUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(html: String) = dispatcherProvider.runCatchingOnDefault {
        postRepository.convertToMarkdown(html)
    }
}