package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FeedScreenState(
    val isFeedLoading: Boolean,
    val count: Int = 0
) : ScreenState
