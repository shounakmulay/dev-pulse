package dev.shounakmulay.feature.feed.screens.feed.ui

import dev.shounakmulay.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
data class FeedScreenState(
    val isLoading: Boolean,
    val count: Int = 0
) : ScreenState