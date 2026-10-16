package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.DiscoveryFeedInteractor
import devpulse.core.resources.generated.resources.discovery_import_failed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class BrowseFeedCollectionViewModel(
    private val collectionId: UUID,
    private val discoveryFeedInteractor: DiscoveryFeedInteractor,
) : MviViewModel<BrowseFeedCollectionScreenState, BrowseFeedCollectionScreenEffect>(
    BrowseFeedCollectionScreenState(collectionId = collectionId)
),
    EventHandler<BrowseFeedCollectionScreenEvent> {

    override fun createStateSerializer() = BrowseFeedCollectionScreenState.serializer()

    private val pagingConfig = PagingConfig(
        pageSize = 20,
        initialLoadSize = 40,
        prefetchDistance = 5,
        enablePlaceholders = false,
    )


    val feedsFlow = discoveryFeedInteractor.collectionFeeds(
        collectionId = collectionId,
        pagingConfig = pagingConfig,
    ).cachedIn(viewModelScope)

    val searchResultsFlow = discoveryFeedInteractor.searchFeeds(
        queries = state.map { it.searchQuery },
        pagingConfig = pagingConfig,
        collectionId = collectionId,
    ).cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        discoveryFeedInteractor.observeSelectedFeed(state.map { it.selectedFeedId }).onEach { (feedId, feed) ->
            setState {
                if (feedId != selectedFeedId) this else copy(selectedFeedId = feed?.id, selectedFeed = feed)
            }
        }.launchIn(stateSubscriptionScope)

        discoveryFeedInteractor.observeCollection(collectionId).onEach { collection ->
            setState {
                copy(
                    content = collection?.let {
                        BrowseFeedCollectionContent.Ready(it)
                    } ?: BrowseFeedCollectionContent.Missing,
                    selectedFeedId = selectedFeedId.takeIf { collection != null },
                )
            }
        }.launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: BrowseFeedCollectionScreenEvent) {
        when (event) {
            is BrowseFeedCollectionScreenEvent.OnSearchQueryChanged -> setState { copy(searchQuery = event.query) }
            is BrowseFeedCollectionScreenEvent.OnAddFeed -> addFeed(event.feedId)
            is BrowseFeedCollectionScreenEvent.OnFeedClick -> setState { copy(selectedFeedId = event.feedId) }
            BrowseFeedCollectionScreenEvent.OnClearSelectedFeed -> setState { copy(selectedFeedId = null, selectedFeed = null) }
        }
    }

    private fun addFeed(feedId: UUID) = intent {
        if (feedId in state.pendingImportIds) return@intent
        setState { copy(pendingImportIds = pendingImportIds + feedId) }
        try {
            discoveryFeedInteractor.addFeed(feedId).onFailure {
                postEffect(
                    BrowseFeedCollectionScreenEffect.ShowToast(
                        TextResource.fromStringRes(
                            stringRes.discovery_import_failed
                        )
                    )
                )
            }
        } finally {
            setState { copy(pendingImportIds = pendingImportIds - feedId) }
        }
    }
}
