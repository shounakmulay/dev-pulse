package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeedWithSearch(
    val feed: RssFeed,
    val highlightedTitle: String? = null,
    val highlightedName: String? = null,
    val highlightedDescription: String? = null,
)
