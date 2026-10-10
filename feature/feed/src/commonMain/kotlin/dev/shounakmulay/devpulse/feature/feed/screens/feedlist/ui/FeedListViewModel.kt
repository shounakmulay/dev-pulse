package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuProcessor
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.dismissMenu
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.toFeedOptionsTarget
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedListInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.Syntax

@KoinViewModel
class FeedListViewModel(
    feedListInteractor: FeedListInteractor,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase,
    private val feedOptionsMenuProcessor: FeedOptionsMenuProcessor,
) : MviViewModel<FeedListScreenState, FeedListScreenEffect>(FeedListScreenState()),
    EventHandler<FeedListScreenEvent> {

    override fun createStateSerializer() = FeedListScreenState.serializer()

    val uiFeedsFlow = feedListInteractor.getUIFeedsFlow(
        sources = state.map { it.feedListSource },
        searchQueries = state.map { it.searchQuery },
    )
        .cachedIn(viewModelScope)

    override fun onEvent(event: FeedListScreenEvent) {
        when (event) {
            is FeedListScreenEvent.OnSearchQueryChanged -> setState {
                copy(searchQuery = event.query, feedOptions = null)
            }
            FeedListScreenEvent.ConfirmDelete -> confirmDelete()
            FeedListScreenEvent.DismissDelete -> intent {
                if (state.feedOptions is FeedOptionsState.ConfirmingDelete) {
                    setState { copy(feedOptions = null) }
                }
            }
            is FeedListScreenEvent.OnFeedOptionSelected -> selectFeedOption(event.feedId, event.menuItem)
            is FeedListScreenEvent.OnShowFeedOptions -> showFeedOptions(event.feed)
            is FeedListScreenEvent.HideFeedOptions -> dismissFeedOptions(event.feedId)
            is FeedListScreenEvent.TogglePinned -> onTogglePinned(event.id, event.pinned)
            is FeedListScreenEvent.SelectTab -> onTabSelected(event.tab)
        }
    }

    private fun onTabSelected(tab: UISelectedTab) {
        setState {
            copy(selectedTab = tab, feedOptions = null)
        }
    }

    private fun onTogglePinned(id: UUID, pinned: Boolean) {
        viewModelScope.launch {
            setFeedPinnedUseCase(id = id, pinned = pinned)
        }
    }

    private fun showFeedOptions(feed: UIFeed) = intent {
        setState { copy(feedOptions = FeedOptionsState.Open(feed.toFeedOptionsTarget())) }
    }

    private fun dismissFeedOptions(feedId: UUID) = intent {
        setState { copy(feedOptions = feedOptions.dismissMenu(feedId)) }
    }

    private fun selectFeedOption(feedId: UUID, option: FeedOptionsMenuItem) = intent {
        val open = state.feedOptions as? FeedOptionsState.Open ?: return@intent
        if (open.target.feedId != feedId || option !in open.target.items) return@intent
        if (option is FeedOptionsMenuItem.Delete) {
            setState { copy(feedOptions = FeedOptionsState.ConfirmingDelete(feedId, option)) }
        } else {
            processFeedOption(feedId = feedId, option = option)
        }
    }

    private fun confirmDelete() = intent {
        val confirmation = state.feedOptions as? FeedOptionsState.ConfirmingDelete ?: return@intent
        processFeedOption(feedId = confirmation.feedId, option = confirmation.option)
    }

    private suspend fun Syntax<FeedListScreenState, FeedListScreenEffect>.processFeedOption(
        feedId: UUID,
        option: FeedOptionsMenuItem,
    ) {
        setState { copy(feedOptions = null) }
        feedOptionsMenuProcessor.processFeedOption(
            feedId = feedId,
            option = option,
            onShare = { postEffect(FeedListScreenEffect.Share(it)) },
            onShowToast = { postEffect(FeedListScreenEffect.ShowToast(it)) },
            onNavigateBack = {},
        )
    }
}
