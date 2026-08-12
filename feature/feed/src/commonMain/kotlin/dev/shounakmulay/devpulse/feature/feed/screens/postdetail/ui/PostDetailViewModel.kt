package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPostDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetRssEncodedContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostDetailViewModel(
    private val postId: UUID,
    private val feedInteractor: FeedInteractor,
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val getPostContentUseCase: GetPostContentUseCase,
    private val getRssEncodedContentUseCase: GetRssEncodedContentUseCase,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase
) : EventHandler<PostDetailScreenEvent>,
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
        stateSubscriptionScope.launch {
            val content =
                getRssEncodedContentUseCase(postId, type = RssFeedPostContentType.RSS_MARKDOWN)
                    .getOrNull()
            setState {
                copy(
                    rssContent = content
                )
            }
        }
        getPostDetailUseCase(postId)
            .onEachSuccess { postWithFeedIdentity ->
                if (postWithFeedIdentity == null) return@onEachSuccess
                setState {
                    copy(
                        isLoading = false,
                        post = feedInteractor.toUIFeedArticle(postWithFeedIdentity)
                    )
                }
            }
            .launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: PostDetailScreenEvent) {
        when (event) {
            is PostDetailScreenEvent.SetPostBookmarked -> {
                intent {
                    setPostBookmarkedUseCase(postId, event.bookmarked)
                }
            }

            is PostDetailScreenEvent.OnSectionSelected -> onSectionSelected(event.section)
        }
    }

    private fun onSectionSelected(section: PostDetailScreenSection) {
        setState {
            copy(
                selectedSection = section
            )
        }
    }

}
