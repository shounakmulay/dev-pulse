package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetFeedDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedPostsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedDetailViewModel(
    private val feedId: String,
    private val feedInteractor: FeedInteractor,
    private val postInteractor: PostInteractor,
    private val getFeedDetailUseCase: GetFeedDetailUseCase,
    private val getPaginatedFeedPostsUseCase: GetPaginatedFeedPostsUseCase,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase
) : MviViewModel<FeedDetailScreenState, FeedDetailScreenEffect>(
    initialState = FeedDetailScreenState(
        sort = postInteractor.getDefaultUIPostSortValues()
    )
),
    EventHandler<FeedDetailScreenEvent> {

    override fun createStateSerializer() = FeedDetailScreenState.serializer()

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = state
        .map { it.filters to it.sort }
        .flatMapLatest { (filters, sort) ->
            feedInteractor.getUIFeedPostFlow(
                getPaginatedFeedPostsUseCase(
                    filters = buildList {
                        add(RssPostFilter.FeedIds(setOf(feedId)))
                        addAll(filters)
                    },
                    sort = sort.firstOrNull { it.selected }?.sort ?: RssPostSort.PublishedNewest
                )
            )
        }
        .cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        getFeedDetailUseCase(feedId)
            .onEach(::updateFeed)
            .launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: FeedDetailScreenEvent) {
        when (event) {
            is FeedDetailScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )

            FeedDetailScreenEvent.Retry -> Unit
            FeedDetailScreenEvent.PinToggled -> onPinToggled()
            is FeedDetailScreenEvent.OnFilterUpdated -> onFilterUpdated(event.filter)
            is FeedDetailScreenEvent.OnSortUpdated -> onSortUpdated(event.sort)
        }
    }

    private fun onSortUpdated(selectedSort: UIPostSort) {
        setState {
            copy(
                sort = sort.map {
                    it.copy(selected = it == selectedSort)
                }.toPersistentList()
            )
        }
    }

    private fun onFilterUpdated(filter: RssPostFilter) {
        setState {
            copy(
                filters = filters.map {
                    if (it::class == filter::class) {
                        filter
                    } else it
                }.toPersistentList()
            )
        }
    }

    private fun onPinToggled() {
        intent {
            state.feed?.let {
                setFeedPinnedUseCase(id = feedId, pinned = !it.pinned)
            }
        }
    }

    private fun onPostBookmarkChanged(postId: String, bookmarked: Boolean) {
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
}
