package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedUserState

data class RssPostWithFeedIdentityAndSearch(
    val post: RssFeedPost,
    val feedIdentity: RssFeedIdentity,
    val search: RssPostSearchHighlights? = null,
    val postState: PostUserState = PostUserState(),
    val feedState: FeedUserState = FeedUserState(),
)
