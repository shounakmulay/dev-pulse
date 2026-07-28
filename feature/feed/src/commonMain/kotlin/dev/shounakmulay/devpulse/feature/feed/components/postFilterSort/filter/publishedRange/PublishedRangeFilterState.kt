package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PublishedRangeFilterState(
    val selectedRange: UIPublishedRange?,
    val rangeValues: List<UIPublishedRange>
) : ScreenState