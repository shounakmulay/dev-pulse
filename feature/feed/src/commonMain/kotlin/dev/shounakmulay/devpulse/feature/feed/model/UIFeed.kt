package dev.shounakmulay.devpulse.feature.feed.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIFeed(
    val id: UUID,
    val imageUrl: String?,
    val title: String,
    val initials: String,
    val pinned: Boolean,
    val sourceUrl: String,
    val websiteImageUrl: String?
)
