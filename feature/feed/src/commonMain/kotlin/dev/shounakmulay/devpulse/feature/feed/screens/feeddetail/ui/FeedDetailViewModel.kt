package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetFeedDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedPostsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.FeedInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedDetailViewModel(
    private val feedId: String,
    private val feedInteractor: FeedInteractor,
    private val getFeedDetailUseCase: GetFeedDetailUseCase,
    private val getPaginatedFeedPostsUseCase: GetPaginatedFeedPostsUseCase,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase
) : MviViewModel<FeedDetailScreenState, FeedDetailScreenEffect>(),
    EventHandler<FeedDetailScreenEvent> {
    override fun createInitialState() = FeedDetailScreenState()

    override fun createStateSerializer() = FeedDetailScreenState.serializer()

    val posts = feedInteractor.getUIFeedPostFlow(
        getPaginatedFeedPostsUseCase(
            feedIds = setOf(feedId),
        )
    ).cachedIn(viewModelScope)

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
