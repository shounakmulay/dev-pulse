package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost

data class LocalRssPostWithFeedAndSearch(
    @Embedded
    val post: LocalRssContentFeedPost,
    @Embedded(prefix = "feed_")
    val feed: LocalRssFeedIdentitySlice,
    @Embedded(prefix = "search_")
    val search: LocalRssPostSearchHighlights? = null
)
