package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.effect.Effect

sealed interface PublishedRangeFilterEffect : Effect {
    data class OnFilterUpdated(val filter: RssPostFilter.PublishedRange) :
        PublishedRangeFilterEffect

    data object OnShowDatePicker : PublishedRangeFilterEffect
}