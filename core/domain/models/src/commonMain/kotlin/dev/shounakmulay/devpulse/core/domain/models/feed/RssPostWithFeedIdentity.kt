package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssPostWithFeedIdentity(
    val post: RssFeedPost,
    val feedIdentity: RssFeedIdentity
)
