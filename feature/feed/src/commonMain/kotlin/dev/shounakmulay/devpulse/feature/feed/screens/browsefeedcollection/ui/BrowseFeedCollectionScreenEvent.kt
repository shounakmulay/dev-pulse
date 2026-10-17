package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface BrowseFeedCollectionScreenEvent : ScreenEvent {
    data class OnSearchQueryChanged(val query: String) : BrowseFeedCollectionScreenEvent
    data class OnFeedClick(val feedId: UUID) : BrowseFeedCollectionScreenEvent
    data object OnClearSelectedFeed : BrowseFeedCollectionScreenEvent
    data class OnAddFeed(val feedId: UUID) : BrowseFeedCollectionScreenEvent
}
