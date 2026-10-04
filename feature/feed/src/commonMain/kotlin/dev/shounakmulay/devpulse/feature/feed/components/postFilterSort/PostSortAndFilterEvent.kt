package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort

sealed interface PostSortAndFilterEvent : ScreenEvent {
    data class OnFilterUpdated(val filter: RssPostFilter) : PostSortAndFilterEvent
    data class OnSortUpdated(val sort: UIPostSort) : PostSortAndFilterEvent
    data object OnClearFilters : PostSortAndFilterEvent
}