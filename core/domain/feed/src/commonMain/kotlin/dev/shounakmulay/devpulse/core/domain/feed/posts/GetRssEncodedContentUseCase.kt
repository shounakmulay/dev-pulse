package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class GetRssEncodedContentUseCase(
    private val postRepository: PostRepository,
    private val convertToMarkdownUseCase: ConvertToMarkdownUseCase,
    private val savePostContentUseCase: SavePostContentUseCase,
    private val applicationScope: ApplicationScope,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(
        postId: UUID,
        type: RssFeedPostContentType
    ) = dispatcherProvider.runCatchingOnDefault {
        require(
            type in setOf(
                RssFeedPostContentType.RSS_HTML,
                RssFeedPostContentType.RSS_MARKDOWN
            )
        ) {
            "Invalid post content type: $type"
        }

        return@runCatchingOnDefault getEncodedContent(postId, type) ?: getEncodedDescription(postId)
    }

    private suspend fun getEncodedContent(
        postId: UUID,
        type: RssFeedPostContentType
    ): RssFeedPostContent? {
        val content = postRepository.getPostRssEncodedContent(
            postId = postId,
            type = type
        )

        if (content != null) {
            return content
        }

        if (type == RssFeedPostContentType.RSS_MARKDOWN) {
            val htmlContent = postRepository.getPostRssEncodedContent(
                postId = postId,
                type = RssFeedPostContentType.RSS_HTML
            ) ?: return null

            val parsedContent =
                convertToMarkdownUseCase(htmlContent.content).getOrNull() ?: return null

            applicationScope.launch {
                savePostContentUseCase(
                    postId = postId,
                    type = RssFeedPostContentType.RSS_MARKDOWN,
                    content = requireNotNull(parsedContent.markdown)
                )
            }
            return RssFeedPostContent(
                postId = postId,
                type = RssFeedPostContentType.RSS_MARKDOWN,
                content = requireNotNull(parsedContent.markdown)
            )
        }

        return null
    }

    private suspend fun getEncodedDescription(postId: UUID): RssFeedPostContent? {
        val description = postRepository.getPostDescription(postId) ?: return null
        val descriptionMarkdown =
            convertToMarkdownUseCase(html = description).getOrNull() ?: return null
        applicationScope.launch {
            savePostContentUseCase(
                postId = postId,
                type = RssFeedPostContentType.RSS_MARKDOWN,
                content = requireNotNull(descriptionMarkdown.markdown)
            )
        }
        return RssFeedPostContent(
            postId = postId,
            type = RssFeedPostContentType.RSS_MARKDOWN,
            content = requireNotNull(descriptionMarkdown.markdown)
        )
    }
}
