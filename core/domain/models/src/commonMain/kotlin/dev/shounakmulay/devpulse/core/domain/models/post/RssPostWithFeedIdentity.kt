package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity

data class RssPostWithFeedIdentity(
    val post: RssFeedPost,
    val feedIdentity: RssFeedIdentity
)