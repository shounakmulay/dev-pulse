package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedListViewModel(
    private val getPaginatedFeedSourcesUseCase: GetPaginatedFeedSourcesUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase,
    private val feedInteractor: FeedInteractor,
) : MviViewModel<FeedListScreenState, FeedListScreenEffect>(FeedListScreenState()),
    EventHandler<FeedListScreenEvent> {

    override fun createStateSerializer() = FeedListScreenState.serializer()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val rssFeedFlow = combine(
        state.map { it.selectedTab }.distinctUntilChanged(),
        state.map { it.searchQuery.trim().takeIf { query -> query.length >= 3 } }
            .distinctUntilChanged()
            .debounce(300.milliseconds),
    ) { tab, query -> tab to query }
        .flatMapLatest { (tab, query) ->
            getPaginatedFeedSourcesUseCase(
                searchQuery = query,
                pinnedOnly = tab == UISelectedTab.PINNED,
            )
        }

    val uiFeedsFlow = feedInteractor.getUIFeedFlow(rssFeedFlow)
        .cachedIn(viewModelScope)

    override fun onEvent(event: FeedListScreenEvent) {
        when (event) {
            is FeedListScreenEvent.OnSearchQueryChanged -> setState { copy(searchQuery = event.query) }
            is FeedListScreenEvent.TogglePinned -> onTogglePinned(event.id, event.pinned)
            is FeedListScreenEvent.SelectTab -> onTabSelected(event.tab)
        }
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
