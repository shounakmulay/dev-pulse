package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.common.flow.debounceSearch
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetFeedDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithUserState
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    initialState = FeedDetailScreenState(),
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

    @OptIn(FlowPreview::class)
    private val searchFlow = state.map {
        it.searchQuery
            .trim()
            .takeIf { query -> query.length >= 3 }
    }.distinctUntilChanged()
        .debounceSearch()

    private val filterAndSortFlow =
        combine(postSortAndFiltersFlow, searchFlow) { (sort, filters), searchQuery ->
            sort to buildList {
                add(RssPostFilter.FeedIds(setOf(feedId)))
                addAll(filters.filterNot { it is RssPostFilter.SearchText })
                searchQuery?.let { add(RssPostFilter.SearchText(searchQuery)) }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = postInteractor
        .getPostsWithFilterAndSort(filerAndSortFlow = filterAndSortFlow)
        .cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        getFeedDetailUseCase(feedId)
            .onEach(::updateFeed)
            .launchIn(stateSubscriptionScope)
    }

    private fun setPostRead(postId: UUID, isRead: Boolean) {
        intent {
            postInteractor.setPostRead(postId = postId, isRead = isRead)
        }
    }

    override fun onEvent(event: FeedDetailScreenEvent) {
        when (event) {
            is FeedDetailScreenEvent.SetPostRead -> setPostRead(event.postId, event.isRead)

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
                bookmarked = event.bookmarked,
            )

            FeedDetailScreenEvent.Retry -> Unit

            is FeedDetailScreenEvent.OnFeedOptionSelected -> selectFeedOption(feedId, event.option)

            FeedDetailScreenEvent.OnPinToggled -> onPinToggled()

            is FeedDetailScreenEvent.OnSearchTextChange -> onSearchTextChange(event.text)
        }
    }

    private fun onSearchTextChange(text: String) {
        blockingSetState {
            copy(searchQuery = text)
        }
    }

    private fun onPinToggled() {
        intent {
            state.feed?.let {
                setFeedPinnedUseCase(id = feedId, pinned = !(state.uiFeed?.pinned ?: false))
            }
        }
    }

    private fun onPostBookmarkChanged(postId: UUID, bookmarked: Boolean) {
        intent {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }

    private suspend fun updateFeed(feed: Result<RssFeedWithUserState>) {
        feed.onSuccess { rssFeed ->
            setState {
                copy(
                    feed = rssFeed.feed,
                    uiFeed = feedInteractor.toUIFeed(rssFeed),
                    isLoading = false,
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
            onFeedDeleted = { postEffect(FeedDetailScreenEffect.FeedDeleted(it)) },
        )
    }
}
