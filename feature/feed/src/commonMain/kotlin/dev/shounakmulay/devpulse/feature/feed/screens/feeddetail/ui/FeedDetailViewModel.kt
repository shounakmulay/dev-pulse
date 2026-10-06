package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetFeedDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveFeedPostListItemVariantUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuProcessor
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.dismissMenu
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.toFeedOptionsTarget
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.Syntax


@KoinViewModel
class FeedDetailViewModel(
    private val feedId: UUID,
    postSortAndFiltersFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>,
    private val feedInteractor: FeedInteractor,
    private val postInteractor: PostInteractor,
    private val getFeedDetailUseCase: GetFeedDetailUseCase,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase,
    private val observeFeedPostListItemVariantUseCase: ObserveFeedPostListItemVariantUseCase,
    private val feedOptionsMenuProcessor: FeedOptionsMenuProcessor,
) : MviViewModel<FeedDetailScreenState, FeedDetailScreenEffect>(
    initialState = FeedDetailScreenState()
),
    EventHandler<FeedDetailScreenEvent> {

    override fun createStateSerializer() = FeedDetailScreenState.serializer()

    init {
        observeFeedPostListItemVariantUseCase()
            .onEachSuccess { variant ->
                if (variant != null) {
                    setState { copy(feedPostListItemVariant = variant) }
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = postInteractor
        .getPostsWithFilterAndSort(filerAndSortFlow = postSortAndFiltersFlow.map {
            it.copy(
                second = buildList {
                    add(RssPostFilter.FeedIds(setOf(feedId)))
                    addAll(it.second)
                }
            )
        })
        .cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        getFeedDetailUseCase(feedId)
            .onEach(::updateFeed)
            .launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: FeedDetailScreenEvent) {
        when (event) {
            FeedDetailScreenEvent.ConfirmDelete -> confirmDelete()
            FeedDetailScreenEvent.DismissDelete -> intent {
                if (state.feedOptions is FeedOptionsState.ConfirmingDelete) {
                    setState { copy(feedOptions = null) }
                }
            }
            is FeedDetailScreenEvent.OnShowFeedOptions -> showFeedOptions(event.feed)
            is FeedDetailScreenEvent.HideFeedOptions -> dismissFeedOptions(event.feedId)
            is FeedDetailScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )

            FeedDetailScreenEvent.Retry -> Unit
            is FeedDetailScreenEvent.OnFeedOptionSelected -> selectFeedOption(feedId, event.option)
            FeedDetailScreenEvent.OnPinToggled -> onPinToggled()
        }
    }

    private fun onPinToggled() {
        intent {
            state.feed?.let {
                setFeedPinnedUseCase(id = feedId, pinned = !it.pinned)
            }
        }
    }

    private fun onPostBookmarkChanged(postId: UUID, bookmarked: Boolean) {
        intent {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }

    private suspend fun updateFeed(feed: Result<RssFeed>) {
        feed.onSuccess { rssFeed ->
            setState {
                copy(
                    feed = rssFeed,
                    uiFeed = feedInteractor.toUIFeed(rssFeed),
                    isLoading = false
                )
            }
        }.onFailure { error ->
            setState { copy(isLoading = false) }
        }
    }

    private fun showFeedOptions(feed: UIFeed) = intent {
        setState { copy(feedOptions = FeedOptionsState.Open(feed.toFeedOptionsTarget(includePin = false))) }
    }

    private fun dismissFeedOptions(feedId: UUID) = intent {
        setState { copy(feedOptions = feedOptions.dismissMenu(feedId)) }
    }

    private fun selectFeedOption(feedId: UUID, option: FeedOptionsMenuItem) = intent {
        val open = state.feedOptions as? FeedOptionsState.Open ?: return@intent
        if (open.target.feedId != feedId || option !in open.target.items) return@intent
        if (option is FeedOptionsMenuItem.Delete) {
            setState { copy(feedOptions = FeedOptionsState.ConfirmingDelete(feedId, option)) }
        } else {
            processFeedOption(feedId = feedId, option = option)
        }
    }

    private fun confirmDelete() = intent {
        val confirmation = state.feedOptions as? FeedOptionsState.ConfirmingDelete ?: return@intent
        processFeedOption(feedId = confirmation.feedId, option = confirmation.option)
    }

    private suspend fun Syntax<FeedDetailScreenState, FeedDetailScreenEffect>.processFeedOption(
        feedId: UUID,
        option: FeedOptionsMenuItem,
    ) {
        setState { copy(feedOptions = null) }
        feedOptionsMenuProcessor.processFeedOption(
            feedId = feedId,
            option = option,
            onShare = { postEffect(FeedDetailScreenEffect.Share(it)) },
            onShowToast = { postEffect(FeedDetailScreenEffect.ShowToast(it)) },
            onNavigateBack = { postEffect(FeedDetailScreenEffect.NavigateBack) },
        )
    }
}
