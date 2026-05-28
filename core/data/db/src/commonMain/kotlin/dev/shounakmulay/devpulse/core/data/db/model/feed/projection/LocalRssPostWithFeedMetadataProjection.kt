package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice

data class LocalRssPostWithFeedMetadataProjection(
    @Embedded
    val post: LocalRssContentFeedPost,
    @Embedded(prefix = "feed_")
    val feed: LocalRssFeedIdentitySlice
)
