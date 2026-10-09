package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedListInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedListSource
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.koin.core.annotation.KoinViewModel

@Serializable
@Immutable
data class FeedListBottomSheetState(
    val searchTerm: String = "",
    val selectedFeedIds: Set<UUID>
) : ScreenState {
    internal val feedListSource: FeedListSource
        get() = searchTerm.trim().let { query ->
            if (query.isBlank()) FeedListSource.All else FeedListSource.Search(query)
        }
}

sealed interface FeedListBottomSheetEffect : Effect

sealed interface FeedListBottomSheetEvent : ScreenEvent {
    data class OnSearchTermChanged(val searchTerm: String) : FeedListBottomSheetEvent
    data class OnFeedToggled(val feedId: UUID) : FeedListBottomSheetEvent
}

@KoinViewModel
class FeedListBottomSheetViewModel(
    preselectedFeedIds: Set<UUID>,
    feedListInteractor: FeedListInteractor,
) : EventHandler<FeedListBottomSheetEvent>,
    MviViewModel<FeedListBottomSheetState, FeedListBottomSheetEffect>(
        initialState = FeedListBottomSheetState(
            selectedFeedIds = preselectedFeedIds
        )
    ) {

    val feeds = combine(
        state.map { it.selectedFeedIds }.distinctUntilChanged(),
        feedListInteractor.getUIFeedsFlow(state.map { it.feedListSource }).cachedIn(viewModelScope)
    ) { selectedFeedIds, pagingData ->
        pagingData.map { feed ->
            val selected = feed.id in selectedFeedIds
            feed to selected
        }
    }

    override fun createStateSerializer(): KSerializer<FeedListBottomSheetState> =
        FeedListBottomSheetState.serializer()

    override fun onEvent(event: FeedListBottomSheetEvent) {
        when (event) {
            is FeedListBottomSheetEvent.OnFeedToggled -> onFeedToggled(event.feedId)
            is FeedListBottomSheetEvent.OnSearchTermChanged -> onSearchTermChanged(event)
        }
    }

    private fun onSearchTermChanged(event: FeedListBottomSheetEvent.OnSearchTermChanged) {
        setState {
            copy(
                searchTerm = event.searchTerm
            )
        }
    }

    private fun onFeedToggled(feedId: UUID) {
        setState {
            copy(
                selectedFeedIds = if (feedId in selectedFeedIds) {
                    selectedFeedIds - feedId
                } else {
                    selectedFeedIds + feedId
                }
            )
        }
    }

}
