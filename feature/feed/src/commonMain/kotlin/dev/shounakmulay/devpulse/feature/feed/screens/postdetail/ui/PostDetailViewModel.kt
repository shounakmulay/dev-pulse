package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPostDetailUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostDetailViewModel(
    private val postId: UUID,
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val getPostContentUseCase: GetPostContentUseCase
) :
    MviViewModel<PostDetailScreenState, PostDetailScreenEffect>(PostDetailScreenState()) {
    override fun createStateSerializer() = PostDetailScreenState.serializer()

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        stateSubscriptionScope.launch {
            val content =
                getPostContentUseCase(postId, type = RssFeedPostContentType.MARKDOWN).getOrNull()
            setState {
                copy(
                    content = content
                )
            }
        }
        getPostDetailUseCase(postId)
            .onEachSuccess {
                setState {
                    copy(
                        isLoading = false,
                        post = it
                    )
                }
            }
            .launchIn(stateSubscriptionScope)
    }
}
