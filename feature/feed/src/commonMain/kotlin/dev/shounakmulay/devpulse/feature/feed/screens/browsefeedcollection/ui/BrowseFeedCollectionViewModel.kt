package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui

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
import devpulse.core.resources.generated.resources.discovery_import_failed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class BrowseFeedCollectionViewModel(
    private val collectionId: UUID,
    private val discoveryFeedInteractor: DiscoveryFeedInteractor,
    private val enqueueFeedDiscoveryImportUseCase: EnqueueFeedDiscoveryImportUseCase,
    private val feedImportBackgroundProcessLauncher: FeedImportBackgroundProcessLauncher,
    logger: DPLogger,
) : MviViewModel<BrowseFeedCollectionScreenState, BrowseFeedCollectionScreenEffect>(
    BrowseFeedCollectionScreenState(collectionId = collectionId)
),
    EventHandler<BrowseFeedCollectionScreenEvent> {

    private companion object {
        const val TAG = "BrowseFeedCollectionViewModel"
    }

    private val log = logger.withTag(TAG)

    override fun createStateSerializer() = BrowseFeedCollectionScreenState.serializer()

    val feeds = discoveryFeedInteractor.getFeeds(
        queries = state.map {
            it.searchQuery
                .trim()
                .takeIf { query -> query.length >= 3 }
        }.distinctUntilChanged()
            .debounceSearch()
            .map { query ->
                query?.let { FeedDiscoveryQuery.Search(text = it, collectionId = collectionId) }
                    ?: FeedDiscoveryQuery.Collection(collectionId)
            }
    ).cachedIn(viewModelScope)

    override fun bindStateSources(stateSubscriptionScope: CoroutineScope) {
        log.d { "Binding discovery screen state collectionId=$collectionId" }
        discoveryFeedInteractor.observeSelectedFeed(state.map { it.selectedFeedId })
            .onEach { (feedId, feed) ->
                setState {
                    if (feedId != selectedFeedId) this else copy(
                        selectedFeedId = feed?.id,
                        selectedFeed = feed
                    )
                }
            }.launchIn(stateSubscriptionScope)

        discoveryFeedInteractor.observeCollection(collectionId).onEach { collection ->
            log.d { "Discovery collection state collectionId=$collectionId found=${collection != null}" }
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
        log.d { "Discovery screen event=${event::class.simpleName} collectionId=$collectionId" }
        when (event) {
            is BrowseFeedCollectionScreenEvent.OnSearchQueryChanged -> onSearchQueryChanged(event)
            is BrowseFeedCollectionScreenEvent.OnAddFeed -> addFeed(event.feedId)
            is BrowseFeedCollectionScreenEvent.OnFeedClick -> onFeedClick(event)
            BrowseFeedCollectionScreenEvent.OnClearSelectedFeed -> onClearSelectedFeed()
        }
    }

    private fun onFeedClick(event: BrowseFeedCollectionScreenEvent.OnFeedClick) {
        setState { copy(selectedFeedId = event.feedId) }
    }

    private fun onClearSelectedFeed() {
        setState {
            copy(
                selectedFeedId = null,
                selectedFeed = null
            )
        }
    }

    private fun onSearchQueryChanged(event: BrowseFeedCollectionScreenEvent.OnSearchQueryChanged) {
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
                BrowseFeedCollectionScreenEffect.ShowToast(
                    TextResource.fromStringRes(stringRes.discovery_import_failed)
                )
            )
        } finally {
            setState { copy(pendingImportIds = pendingImportIds - feedId) }
        }
    }
}
