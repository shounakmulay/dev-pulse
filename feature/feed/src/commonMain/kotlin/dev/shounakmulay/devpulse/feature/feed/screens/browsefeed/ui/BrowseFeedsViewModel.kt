package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.DiscoveryFeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui.BrowseFeedsScreenEffect.OpenCollection
import devpulse.core.resources.generated.resources.discovery_import_failed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class BrowseFeedsViewModel(
    private val discoveryFeedInteractor: DiscoveryFeedInteractor,
) : MviViewModel<BrowseFeedsScreenState, BrowseFeedsScreenEffect>(BrowseFeedsScreenState()),
    EventHandler<BrowseFeedsScreenEvent> {

    override fun createStateSerializer() = BrowseFeedsScreenState.serializer()

    private val pagingConfig = PagingConfig(
        pageSize = 20,
        initialLoadSize = 40,
        prefetchDistance = 5,
        enablePlaceholders = false,
    )


    val searchResultsFlow = discoveryFeedInteractor.searchFeeds(
        queries = state.map { it.searchQuery },
        pagingConfig = pagingConfig,
    ).cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        discoveryFeedInteractor.observeSelectedFeed(state.map { it.selectedFeedId }).onEach { (feedId, feed) ->
            setState {
                if (feedId != selectedFeedId) this else copy(selectedFeedId = feed?.id, selectedFeed = feed)
            }
        }.launchIn(stateSubscriptionScope)

        discoveryFeedInteractor.observeCollectionShelves().onEach { shelves ->
            setState { copy(collectionShelves = shelves) }
        }.launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: BrowseFeedsScreenEvent) {
        when (event) {
            is BrowseFeedsScreenEvent.OnSearchQueryChanged -> setState { copy(searchQuery = event.query) }
            is BrowseFeedsScreenEvent.OnAddFeed -> addFeed(event.feedId)
            is BrowseFeedsScreenEvent.OnViewAll -> postEffect(OpenCollection(event.collectionId))
            is BrowseFeedsScreenEvent.OnFeedClick -> setState { copy(selectedFeedId = event.feedId) }
            BrowseFeedsScreenEvent.OnClearSelectedFeed -> setState { copy(selectedFeedId = null, selectedFeed = null) }
        }
    }


    private fun addFeed(feedId: UUID) = intent {
        if (feedId in state.pendingImportIds) return@intent
        setState { copy(pendingImportIds = pendingImportIds + feedId) }
        try {
            discoveryFeedInteractor.addFeed(feedId).onFailure {
                postEffect(BrowseFeedsScreenEffect.ShowToast(TextResource.fromStringRes(stringRes.discovery_import_failed)))
            }
        } finally {
            setState { copy(pendingImportIds = pendingImportIds - feedId) }
        }
    }
}
