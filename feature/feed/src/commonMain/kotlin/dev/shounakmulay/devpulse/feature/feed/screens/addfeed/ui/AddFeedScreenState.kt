package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.feed.AddFeedData
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AddFeedScreenState(
    val isLoading: Boolean = false,
    val addFeedDataList: List<AddFeedData> = listOf(
        AddFeedData.empty()
    )
) : ScreenState
