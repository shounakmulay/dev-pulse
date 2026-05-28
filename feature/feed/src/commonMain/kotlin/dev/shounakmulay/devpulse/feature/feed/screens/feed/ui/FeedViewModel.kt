package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedViewModel(
    private val feedInteractor: FeedInteractor,
) : MviViewModel<FeedScreenState, FeedScreenEffect>(),
    EventHandler<FeedScreenEvent> {
    override fun createInitialState(): FeedScreenState = FeedScreenState()
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
            is FeedScreenEvent.OnFeedLongClick -> onFeedLongClick(event.feed)
        }
    }

    private fun onFeedLongClick(feed: UIFeed) {
        // Handle long click logic, such as showing a bottom sheet or pinning
    }
}
