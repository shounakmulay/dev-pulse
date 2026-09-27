package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedImage

data class LocalRssFeedSearchResult(
    val id: LocalUUID,
    val link: String?,
    val sourceUrl: String,
    @Embedded(prefix = "image_")
    val image: LocalRssFeedImage?,
    val pinned: Boolean,
    val highlightedTitle: String,
    val highlightedName: String,
    val highlightedDescription: String,
)
