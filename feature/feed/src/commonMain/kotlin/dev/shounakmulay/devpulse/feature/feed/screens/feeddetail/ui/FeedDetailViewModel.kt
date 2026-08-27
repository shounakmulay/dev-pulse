package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetFeedDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel


@KoinViewModel
class FeedDetailViewModel(
    private val feedId: UUID,
    postSortAndFiltersFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>,
    private val feedInteractor: FeedInteractor,
    private val postInteractor: PostInteractor,
    private val getFeedDetailUseCase: GetFeedDetailUseCase,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase
) : MviViewModel<FeedDetailScreenState, FeedDetailScreenEffect>(
    initialState = FeedDetailScreenState()
),
    EventHandler<FeedDetailScreenEvent> {

    override fun createStateSerializer() = FeedDetailScreenState.serializer()

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
            is FeedDetailScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )

            FeedDetailScreenEvent.Retry -> Unit
            FeedDetailScreenEvent.PinToggled -> onPinToggled()
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
}
