package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
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
    private val parsePostContentUseCase: ParsePostContentUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val applicationScope: ApplicationScope
) {
    suspend operator fun invoke(
        postId: UUID,
        type: RssFeedPostContentType
    ) = dispatcherProvider.runCatchingOnDefault {
        val content = postRepository.getPostContent(
            postId = postId,
            type = type
        )

        if (content != null) {
            return@runCatchingOnDefault content
        }

        val parsedContent = parsePostContentUseCase(postId, type).getOrNull()

        if (parsedContent != null) {
            saveContent(parsedContent, postId)
        }

        when (type) {
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

            RssFeedPostContentType.JSON -> null
        }
    }

    private fun saveContent(
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

