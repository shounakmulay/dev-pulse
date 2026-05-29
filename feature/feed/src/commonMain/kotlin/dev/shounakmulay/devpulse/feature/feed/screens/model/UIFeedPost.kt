package dev.shounakmulay.devpulse.feature.feed.screens.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIFeedPost(
    val id: String,
    val title: String,
    val sourceName: String,
    val sourceUrl: String?,
    val articleUrl: String?,
    val publishedText: String?,
    val imageUrl: String?,
    val summary: String?,
    val feed: UIFeed
)
