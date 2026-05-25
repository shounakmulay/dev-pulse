package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UIFeedQueueData(
    val addFeedData: UIAddFeedData,
    val status: RssFeedQueueStatus?
)