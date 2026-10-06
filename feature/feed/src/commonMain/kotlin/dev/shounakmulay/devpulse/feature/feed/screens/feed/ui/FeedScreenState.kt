package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class FeedScreenState(
    val isFeedLoading: Boolean = true,
    val isArticlesLoading: Boolean = true,
    @Transient
    val feedOptions: FeedOptionsState? = null
) : ScreenState
