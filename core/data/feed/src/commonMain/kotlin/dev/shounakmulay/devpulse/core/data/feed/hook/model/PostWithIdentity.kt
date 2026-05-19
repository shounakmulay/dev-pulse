package dev.shounakmulay.devpulse.core.data.feed.hook.model

import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentity

data class PostWithIdentity(
    val post: LocalRssContentFeedPost,
    val identity: LocalRssContentFeedPostIdentity?
)
