package dev.shounakmulay.devpulse.feature.feed.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIFeedSearchResult(
    val id: UUID,
    val imageUrl: String?,
    val initials: String,
    val sourceUrl: String,
    val title: TextResource,
    val description: TextResource,
    val pinned: Boolean,
    val websiteImageUrl: String?
)