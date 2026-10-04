package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed

data class LocalRssFeedWithSearch(
    @Embedded val feed: LocalRssFeed,
    val highlightedTitle: String? = null,
    val highlightedName: String? = null,
    val highlightedDescription: String? = null,
)
