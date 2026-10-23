package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.common.flow.debounceSearch
import dev.shounakmulay.devpulse.core.domain.feed.discovery.EnqueueFeedDiscoveryImportUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryImportState
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.logging.log.DPLogger
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.DiscoveryFeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.service.FeedImportBackgroundProcessLauncher
import dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui.BrowseFeedsScreenEffect.OpenCollection
import devpulse.core.resources.generated.resources.discovery_import_failed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class BrowseFeedsViewModel(
    private val discoveryFeedInteractor: DiscoveryFeedInteractor,
    private val enqueueFeedDiscoveryImportUseCase: EnqueueFeedDiscoveryImportUseCase,
    private val feedImportBackgroundProcessLauncher: FeedImportBackgroundProcessLauncher,
    logger: DPLogger,
) : MviViewModel<BrowseFeedsScreenState, BrowseFeedsScreenEffect>(BrowseFeedsScreenState()),
    EventHandler<BrowseFeedsScreenEvent> {

    private companion object {
        const val TAG = "BrowseFeedsViewModel"
    }

    private val log = logger.withTag(TAG)

    override fun createStateSerializer() = BrowseFeedsScreenState.serializer()

    val feeds = discoveryFeedInteractor.getFeeds(
        queries = state.map {
            it.searchQuery
                .trim()
                .takeIf { query -> query.length >= 3 }
        }.distinctUntilChanged()
            .debounceSearch()
            .map { query -> query?.let { FeedDiscoveryQuery.Search(it) } }
    ).cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        log.d { "Binding discovery screen state" }
        discoveryFeedInteractor.observeSelectedFeed(state.map { it.selectedFeedId })
            .onEach { (feedId, feed) ->
                setState {
                    if (feedId != selectedFeedId) this else copy(
                        selectedFeedId = feed?.id,
                        selectedFeed = feed
                    )
                }
            }.launchIn(stateSubscriptionScope)

        discoveryFeedInteractor.observeCollectionShelves().onEach { shelves ->
            log.d { "Discovery shelves ready collections=${shelves.size}" }
            setState { copy(collectionShelves = shelves, isLoading = false) }
        }.launchIn(stateSubscriptionScope)
    }

    override fun onEvent(event: BrowseFeedsScreenEvent) {
        log.d { "Discovery screen event=${event::class.simpleName}" }
        when (event) {
            is BrowseFeedsScreenEvent.OnSearchQueryChanged -> onSearchQueryChanged(event)
            is BrowseFeedsScreenEvent.OnAddFeed -> addFeed(event.feedId)
            is BrowseFeedsScreenEvent.OnViewAll -> {
                log.d { "Opening discovery collection collectionId=${event.collectionId}" }
                postEffect(OpenCollection(event.collectionId))
            }

            is BrowseFeedsScreenEvent.OnFeedClick -> onFeedClick(event)
            BrowseFeedsScreenEvent.OnClearSelectedFeed -> onClearSelectedFeed()
        }
    }

    private fun onFeedClick(event: BrowseFeedsScreenEvent.OnFeedClick) {
        setState { copy(selectedFeedId = event.feedId) }
    }

    private fun onClearSelectedFeed() {
        setState { copy(selectedFeedId = null, selectedFeed = null) }
    }

    private fun onSearchQueryChanged(event: BrowseFeedsScreenEvent.OnSearchQueryChanged) {
        blockingSetState { copy(searchQuery = event.query) }
    }


    private fun addFeed(feedId: UUID) = intent {
        if (feedId in state.pendingImportIds) {
            log.d { "Skipping duplicate discovery import request feedId=$feedId" }
            return@intent
        }
        setState { copy(pendingImportIds = pendingImportIds + feedId) }
        try {
            val importState = enqueueFeedDiscoveryImportUseCase(feedId).getOrThrow()
            if (importState == FeedDiscoveryImportState.Queued) {
                feedImportBackgroundProcessLauncher.launch()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (cause: Exception) {
            log.e(cause) { "Discovery import failed feedId=$feedId" }
            postEffect(
                BrowseFeedsScreenEffect.ShowToast(
                    TextResource.fromStringRes(stringRes.discovery_import_failed)
                )
            )
        } finally {
            setState { copy(pendingImportIds = pendingImportIds - feedId) }
        }
    }
}
