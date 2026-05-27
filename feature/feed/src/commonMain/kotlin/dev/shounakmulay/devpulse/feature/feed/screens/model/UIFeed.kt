package dev.shounakmulay.devpulse.feature.feed.screens.model

import androidx.compose.runtime.Immutable

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
