package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface PublishedRangeFilterEvent : ScreenEvent {
    data class OnFilterUpdated(val filter: UIPublishedRange?) : PublishedRangeFilterEvent
}