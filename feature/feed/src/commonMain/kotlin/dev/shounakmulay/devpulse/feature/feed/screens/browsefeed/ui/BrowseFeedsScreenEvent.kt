package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface BrowseFeedsScreenEvent : ScreenEvent {
    data class OnSearchQueryChanged(val query: String) : BrowseFeedsScreenEvent
    data class OnAddFeed(val feedId: UUID) : BrowseFeedsScreenEvent
    data class OnViewAll(val collectionId: UUID) : BrowseFeedsScreenEvent
    data class OnFeedClick(val feedId: UUID): BrowseFeedsScreenEvent
    data object OnClearSelectedFeed: BrowseFeedsScreenEvent

}
