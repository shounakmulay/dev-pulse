package dev.shounakmulay.devpulse.feature.feed.screens.feedsearch.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.feed.SearchFeedsUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedSearchInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@KoinViewModel
class FeedSearchViewModel(
    private val searchFeedsUseCase: SearchFeedsUseCase,
    private val feedSearchInteractor: FeedSearchInteractor,
) : MviViewModel<FeedSearchScreenState, FeedSearchScreenEffect>(FeedSearchScreenState()),
    EventHandler<FeedSearchScreenEvent> {

    override fun createStateSerializer() = FeedSearchScreenState.serializer()

    init {
        state.map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .mapLatest { query ->
                setState { copy(searchLoading = true) }
                val result = searchFeedsUseCase(query)
                val uiResults = feedSearchInteractor.getUIFeedSearchResults(result.getOrNull())
                setState {
                    if (searchQuery == query) {
                        copy(searchLoading = false, searchResults = uiResults)
                    } else {
                        this
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: FeedSearchScreenEvent) {
        viewModelScope.launch {
            cancel()
        }
        when (event) {
            is FeedSearchScreenEvent.OnSearchQueryChanged -> setState {
                copy(searchQuery = event.query)
            }
        }
    }
}
