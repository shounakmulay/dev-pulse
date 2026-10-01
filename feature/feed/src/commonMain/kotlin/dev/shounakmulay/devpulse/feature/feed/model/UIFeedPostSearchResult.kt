package dev.shounakmulay.devpulse.feature.feed.model

import kotlinx.serialization.Serializable

@Serializable
data class UIFeedPostSearchResult(
    val post: UIFeedPost,
    val feed: UIFeed,
    val search: UIFeedPostSearchHighlights?
)
