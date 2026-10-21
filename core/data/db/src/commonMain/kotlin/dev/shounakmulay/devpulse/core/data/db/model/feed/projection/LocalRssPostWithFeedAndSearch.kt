package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalPostUserStateSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedUserStateSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost

data class LocalRssPostWithFeedAndSearch(
    @Embedded
    val post: LocalRssContentFeedPost,
    @Embedded(prefix = "feed_")
    val feed: LocalRssFeedIdentitySlice,
    @Embedded(prefix = "search_")
    val search: LocalRssPostSearchHighlights? = null,
    @Embedded(prefix = "postState_")
    val postState: LocalPostUserStateSlice = LocalPostUserStateSlice(),
    @Embedded(prefix = "feedState_")
    val feedState: LocalFeedUserStateSlice = LocalFeedUserStateSlice(),
)
