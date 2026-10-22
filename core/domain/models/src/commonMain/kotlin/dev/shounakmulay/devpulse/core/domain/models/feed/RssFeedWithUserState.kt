package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeedWithUserState(
    val feed: RssFeed,
    val userState: FeedUserState = FeedUserState(),
)
