package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import kotlinx.coroutines.async
import org.koin.core.annotation.Factory

@Factory
class SearchPostsUseCase(
    private val postRepository: PostRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(query: String) = dispatcherProvider.runCatchingOnDefault {
        val postResults = async {
            postRepository.searchPosts(
                query = query,
                snippetLength = 30,
                limit = 50
            )
        }
        val postContentResults = async {
            postRepository.searchPostContent(
                query = query,
                snippetLength = 40,
                limit = 50
            )
        }

        val posts = postResults.await()
        val postContent = postContentResults.await().toMutableList()

        posts
            .map { post ->
                val content = postContent.find { postContent ->
                    post.post.id == postContent.post.id
                } ?: return@map post

                val searchHighlights = post.search ?: return@map post
                val contentSearchHighlights = content.search?.highlightedContent ?: return@map post

                postContent.remove(content)
                post.copy(
                    search = searchHighlights.copy(highlightedContent = contentSearchHighlights)
                )
            }
            .plus(postContent)
    }
}
