package dev.shounakmulay.devpulse.core.data.feed.hook.model

import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost

data class LocalPostWithIdentity(
    val post: LocalRssContentFeedPost,
    val identity: LocalRssContentFeedPostIdentitySlice?
)
