package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
data class FeedScreenState(
    val isLoading: Boolean,
    val count: Int = 0
) : ScreenState