package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class FeedListScreenState(
    @Transient
    val feedOptions: FeedOptionsState? = null,
    val selectedTab: UISelectedTab = UISelectedTab.ALL,
) : ScreenState
