package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class FeedDetailScreenState(
    val isLoading: Boolean = true,
    @Transient
    val feed: RssFeed? = null,
    val uiFeed: UIFeed? = null,
) : ScreenState
