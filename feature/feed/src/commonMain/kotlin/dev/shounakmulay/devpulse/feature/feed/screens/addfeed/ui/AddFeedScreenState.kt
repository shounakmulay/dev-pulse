package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIFeedQueueData
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AddFeedScreenState(
    val isLoading: Boolean = false,
    val addFeedDataList: List<UIAddFeedData> = listOf(
        UIAddFeedData.empty(),
    ),
    val processingData: List<UIFeedQueueData> = listOf()
) : ScreenState
