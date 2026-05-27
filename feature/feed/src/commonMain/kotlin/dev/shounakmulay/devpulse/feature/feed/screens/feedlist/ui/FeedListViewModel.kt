package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedPinnedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedListViewModel(
    private val getPaginatedFeedSourcesUseCase: GetPaginatedFeedSourcesUseCase,
    private val getPaginatedPinnedFeedSourcesUseCase: GetPaginatedPinnedFeedSourcesUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase,
    private val feedInteractor: FeedInteractor,
) : MviViewModel<FeedListScreenState, FeedListScreenEffect>(),
    EventHandler<FeedListScreenEvent> {
    override fun createInitialState() = FeedListScreenState()

    override fun createStateSerializer() = FeedListScreenState.serializer()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rssFeedFlow = state.map { it.selectedTab }
        .distinctUntilChanged()
        .flatMapLatest {
            when (it) {
                UISelectedTab.ALL -> getPaginatedFeedSourcesUseCase()
                UISelectedTab.PINNED -> getPaginatedPinnedFeedSourcesUseCase()
            }
        }

    val uiFeedsFlow = feedInteractor.getUIFeedFlow(rssFeedFlow)
        .cachedIn(viewModelScope)

    override fun onEvent(event: FeedListScreenEvent) {
        when (event) {
            is FeedListScreenEvent.TogglePinned -> onTogglePinned(event.id, event.pinned)
            is FeedListScreenEvent.SelectTab -> onTabSelected(event.tab)
        }
    }

    private fun onTabSelected(tab: UISelectedTab) {
        setState {
            copy(selectedTab = tab)
        }
    }

    private fun onTogglePinned(id: String, pinned: Boolean) {
        viewModelScope.launch {
            setFeedPinnedUseCase(id = id, pinned = pinned)
        }
    }
}
