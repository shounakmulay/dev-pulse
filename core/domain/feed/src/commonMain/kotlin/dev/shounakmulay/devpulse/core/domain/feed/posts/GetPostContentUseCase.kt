package dev.shounakmulay.devpulse.core.domain.feed.posts

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.ConvertToMarkdownUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.models.post.RssParsedPostContent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class GetPostContentUseCase(
    private val postRepository: PostRepository,
    private val savePostContentUseCase: SavePostContentUseCase,
    private val fetchPostContentUseCase: FetchPostContentUseCase,
    private val convertPostContentUseCase: ConvertToMarkdownUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val applicationScope: ApplicationScope
) {
    suspend operator fun invoke(
        postId: UUID,
        type: RssFeedPostContentType
    ) = dispatcherProvider.runCatchingOnDefault {
        require(
            type in setOf(
                RssFeedPostContentType.HTML,
                RssFeedPostContentType.MARKDOWN
            )
        ) {
            "Invalid post content type: $type"
        }

        val content = postRepository.getPostContent(
            postId = postId,
            type = type
        )

        if (content != null) {
            return@runCatchingOnDefault content
        }

        var parsedContent: RssParsedPostContent? = null

        if (type == RssFeedPostContentType.MARKDOWN) {
            parsedContent = tryParseFromHtml(postId)
        }

        if (parsedContent == null) {
            parsedContent = fetchPostContentUseCase(postId, type).getOrNull()
        }

        if (parsedContent != null) {
            saveContentAsync(parsedContent, postId)
        }

        feedPostContent(type, postId, parsedContent)
    }

    private suspend fun tryParseFromHtml(
        postId: UUID,
    ): RssParsedPostContent? {
        val htmlContent = postRepository.getPostContent(
            postId = postId,
            type = RssFeedPostContentType.HTML
        ) ?: return null
        return convertPostContentUseCase(htmlContent.content).getOrNull()
    }

    private fun feedPostContent(
        type: RssFeedPostContentType,
        postId: UUID,
        parsedContent: RssParsedPostContent?
    ): RssFeedPostContent? = when (type) {
        RssFeedPostContentType.HTML -> {
            RssFeedPostContent(
                postId = postId,
                type = type,
                content = requireNotNull(parsedContent?.html)
            )
        }

        RssFeedPostContentType.MARKDOWN -> RssFeedPostContent(
            postId = postId,
            type = type,
            content = requireNotNull(parsedContent?.markdown)
        )

        else -> null
    }

    private fun saveContentAsync(
        parsedContent: RssParsedPostContent,
        postId: UUID,
    ) = applicationScope.launch {
        buildList {
            parsedContent.html?.let {
                add(
                    async {
                        savePostContentUseCase(
                            postId = postId,
                            type = RssFeedPostContentType.HTML,
                            content = it
                        )
                    }
                )
            }

            parsedContent.markdown?.let {
                add(
                    async {
                        savePostContentUseCase(
                            postId = postId,
                            type = RssFeedPostContentType.MARKDOWN,
                            content = it
                        )
                    }
                )
            }
        }.awaitAll()
    }
}

