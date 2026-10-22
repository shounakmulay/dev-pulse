package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

import androidx.room3.Embedded
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalFeedUserStateSlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed

data class LocalRssFeedWithUserState(
    @Embedded val feed: LocalRssFeed,
    @Embedded(prefix = "state_") val state: LocalFeedUserStateSlice = LocalFeedUserStateSlice(),
)
