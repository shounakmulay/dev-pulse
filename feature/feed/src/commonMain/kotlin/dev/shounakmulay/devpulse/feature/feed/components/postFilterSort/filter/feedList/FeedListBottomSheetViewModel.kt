package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.koin.core.annotation.KoinViewModel

@Serializable
data class FeedListBottomSheetState(
    val searchTerm: String = "",
    val selectedFeedIds: Set<String>
) : ScreenState

sealed class FeedListBottomSheetEffect : Effect {}

sealed interface FeedListBottomSheetEvent : ScreenEvent {
    data class OnSearchTermChanged(val searchTerm: String) : FeedListBottomSheetEvent
    data class OnFeedToggled(val feedId: String) : FeedListBottomSheetEvent
}

@KoinViewModel
class FeedListBottomSheetViewModel(
    preselectedFeedIds: Set<String>,
    feedInteractor: FeedInteractor,
    getPaginatedFeedSourcesUseCase: GetPaginatedFeedSourcesUseCase
) : EventHandler<FeedListBottomSheetEvent>,
    MviViewModel<FeedListBottomSheetState, FeedListBottomSheetEffect>(
        initialState = FeedListBottomSheetState(
            selectedFeedIds = preselectedFeedIds
        )
    ) {

    val feeds = combine(
        state.map { it.selectedFeedIds },
        feedInteractor.getUIFeedFlow(getPaginatedFeedSourcesUseCase()).cachedIn(viewModelScope)
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

    private fun onFeedToggled(feedId: String) {
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