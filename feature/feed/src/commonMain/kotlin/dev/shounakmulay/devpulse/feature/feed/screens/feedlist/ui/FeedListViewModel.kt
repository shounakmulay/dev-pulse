package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedPinnedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SearchFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedSearchInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedListViewModel(
    private val getPaginatedFeedSourcesUseCase: GetPaginatedFeedSourcesUseCase,
    private val getPaginatedPinnedFeedSourcesUseCase: GetPaginatedPinnedFeedSourcesUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase,
    private val searchFeedsUseCase: SearchFeedsUseCase,
    private val feedInteractor: FeedInteractor,
    private val feedSearchInteractor: FeedSearchInteractor
) : MviViewModel<FeedListScreenState, FeedListScreenEffect>(FeedListScreenState()),
    EventHandler<FeedListScreenEvent> {

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
            is FeedListScreenEvent.Search -> onSearch(event.query)
        }
    }

    private fun onSearch(query: String) = intent {
        setState { copy(searchLoading = true) }
        val result = searchFeedsUseCase(query)
        val uiResults = feedSearchInteractor.getUIFeedSearchResults(result.getOrNull())
        setState { copy(searchLoading = false, searchResults = uiResults) }
    }

    private fun onTabSelected(tab: UISelectedTab) {
        setState {
            copy(selectedTab = tab)
        }
    }

    private fun onTogglePinned(id: UUID, pinned: Boolean) {
        viewModelScope.launch {
            setFeedPinnedUseCase(id = id, pinned = pinned)
        }
    }
}
