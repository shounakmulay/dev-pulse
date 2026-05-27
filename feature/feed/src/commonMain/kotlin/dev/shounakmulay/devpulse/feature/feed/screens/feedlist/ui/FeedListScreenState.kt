package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FeedListScreenState(
    val selectedTab: UISelectedTab = UISelectedTab.ALL,
) : ScreenState
