package dev.shounakmulay.devpulse.feature.feed.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIFeed(
    val id: String,
    val imageUrl: String?,
    val title: String,
    val initials: String,
    val pinned: Boolean,
    val sourceUrl: String,
    val websiteImageUrl: String?
)
