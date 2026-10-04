package dev.shounakmulay.devpulse.feature.feed.screens.feedsearch.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedSearchResult
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FeedSearchScreenState(
    val searchQuery: String = "",
    val searchLoading: Boolean = false,
    val searchResults: List<UIFeedSearchResult> = emptyList(),
) : ScreenState
