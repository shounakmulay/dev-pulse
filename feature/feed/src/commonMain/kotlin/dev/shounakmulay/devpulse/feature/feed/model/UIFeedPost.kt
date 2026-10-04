package dev.shounakmulay.devpulse.feature.feed.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIFeedPost(
    val id: UUID,
    val title: String,
    val sourceName: String,
    val sourceUrl: String?,
    val articleUrl: String?,
    val publishedText: String?,
    val imageUrl: String?,
    val summary: String?,
    val bookmarked: Boolean,
    val createdAt: String,
    val feed: UIFeed,
    val search: UIFeedPostSearchHighlights? = null
)
