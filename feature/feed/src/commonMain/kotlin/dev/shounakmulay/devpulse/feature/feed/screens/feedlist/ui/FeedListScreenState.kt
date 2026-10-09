package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedListSource
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class FeedListScreenState(
    @Transient
    val feedOptions: FeedOptionsState? = null,
    val selectedTab: UISelectedTab = UISelectedTab.ALL,
    val searchQuery: String = "",
) : ScreenState {
    internal val feedListSource: FeedListSource
        get() {
            val query = searchQuery.trim()
            return when {
                query.isNotBlank() -> FeedListSource.Search(query)
                selectedTab == UISelectedTab.PINNED -> FeedListSource.Pinned
                else -> FeedListSource.All
            }
        }
}
