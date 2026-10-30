package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPostDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetRssEncodedContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.settings.content.GetBrowserOpeningTargetUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingFontScaleUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingLineHeightScaleUseCase
import dev.shounakmulay.devpulse.core.logging.log.DPLogger
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.ReaderVisitReadDecision
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostDetailViewModel(
    private val postId: UUID,
    private val postInteractor: PostInteractor,
    private val getBrowserOpeningTargetUseCase: GetBrowserOpeningTargetUseCase,
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val getPostContentUseCase: GetPostContentUseCase,
    private val getRssEncodedContentUseCase: GetRssEncodedContentUseCase,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setContentTextSettingFontScaleUseCase: SetContentTextSettingFontScaleUseCase,
    private val setContentTextSettingLineHeightScaleUseCase: SetContentTextSettingLineHeightScaleUseCase,
    logger: DPLogger,
) : MviViewModel<PostDetailScreenState, PostDetailScreenEffect>(PostDetailScreenState()),
    EventHandler<PostDetailScreenEvent> {

    private val log = logger.withTag("PostDetailViewModel")

    override fun createStateSerializer() = PostDetailScreenState.serializer()

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        stateSubscriptionScope.launch {
            val content =
                getPostContentUseCase(postId, type = RssFeedPostContentType.MARKDOWN).getOrNull()
            setState {
                copy(
                    content = content,
                )
            }
        }
        stateSubscriptionScope.launch {
            val content =
                getRssEncodedContentUseCase(postId, type = RssFeedPostContentType.RSS_MARKDOWN)
                    .getOrNull()
            setState {
                copy(
                    rssContent = content,
                )
            }
        }
        getPostDetailUseCase(postId)
            .onEachSuccess { postWithFeedIdentity ->
                if (postWithFeedIdentity == null) return@onEachSuccess
                setState {
                    copy(
                        isLoading = false,
                        post = postInteractor.toUIFeedPost(postWithFeedIdentity),
                    )
                }
            }
            .launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: PostDetailScreenEvent) {
        when (event) {
            is PostDetailScreenEvent.OpenInBrowser -> intent {
                val target = getBrowserOpeningTargetUseCase(event.url).onFailure {
                    log.e(throwable = it) { "Couldn't read browser opening preference" }
                }.getOrNull() ?: BrowserOpeningTarget.External(event.url)
                postEffect(PostDetailScreenEffect.OpenInBrowser(target))
            }

            PostDetailScreenEvent.ContentAvailable -> onContentAvailable()

            is PostDetailScreenEvent.SetPostRead -> setPostRead(event.postId, event.isRead)

            is PostDetailScreenEvent.SetPostBookmarked -> setPostBookmarked(event)

            is PostDetailScreenEvent.OnSectionSelected -> onSectionSelected(event.section)

            is PostDetailScreenEvent.SetContentLineHeightScale -> setContentLineHeightScale(event.scale)

            is PostDetailScreenEvent.SetContentTextScale -> setContentTextScale(event.scale)
        }
    }

    private fun onContentAvailable() {
        intent {
            if (state.post == null || state.readVisit.decision != ReaderVisitReadDecision.Pending) return@intent
            reduce { state.copy(readVisit = state.readVisit.consume()) }
            postInteractor.setPostRead(postId = postId, isRead = true)
        }
    }

    private fun setPostRead(postId: UUID, isRead: Boolean) {
        intent {
            if (postId != this@PostDetailViewModel.postId) return@intent
            reduce { state.copy(readVisit = state.readVisit.suppress()) }
            postInteractor.setPostRead(postId = postId, isRead = isRead)
        }
    }

    private fun setPostBookmarked(event: PostDetailScreenEvent.SetPostBookmarked) {
        intent {
            setPostBookmarkedUseCase(postId, event.bookmarked)
        }
    }

    private fun setContentTextScale(scale: Float) {
        intent {
            setContentTextSettingFontScaleUseCase(scale)
        }
    }

    private fun setContentLineHeightScale(scale: Float) {
        intent {
            setContentTextSettingLineHeightScaleUseCase(scale)
        }
    }

    private fun onSectionSelected(section: PostDetailScreenSection) {
        setState {
            copy(
                selectedSection = section,
            )
        }
    }
}
