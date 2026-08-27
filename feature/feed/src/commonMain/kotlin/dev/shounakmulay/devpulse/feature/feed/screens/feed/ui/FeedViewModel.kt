package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedViewModel(
    private val feedInteractor: FeedInteractor,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase
) : MviViewModel<FeedScreenState, FeedScreenEffect>(FeedScreenState()),
    EventHandler<FeedScreenEvent> {
    override fun createStateSerializer() = FeedScreenState.serializer()

    val pinnedAndRecentFeeds = feedInteractor
        .getPinnedAndRecentsUIFeedFlow()
        .onEach {
            setState {
                copy(
                    isFeedLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf()
        )

    val recentArticles = feedInteractor
        .getRecentArticlesFlow()
        .onEach {
            setState {
                copy(
                    isArticlesLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf()
        )

    override fun onEvent(event: FeedScreenEvent) {
        when (event) {
            is FeedScreenEvent.OnFeedLongClick -> Unit
            is FeedScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )

            is FeedScreenEvent.OnFeedPinChanged -> onFeedPinChanged(
                feedId = event.feedId,
                pinned = event.pinned
            )
        }
    }

    private fun onPostBookmarkChanged(postId: UUID, bookmarked: Boolean) {
        viewModelScope.launch {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }

    private fun onFeedPinChanged(feedId: UUID, pinned: Boolean) {
        viewModelScope.launch {
            setFeedPinnedUseCase(id = feedId, pinned = pinned)
        }
    }
}
