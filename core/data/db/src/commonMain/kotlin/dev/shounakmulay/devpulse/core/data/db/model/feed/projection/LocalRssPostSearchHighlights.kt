package dev.shounakmulay.devpulse.core.data.db.model.feed.projection

data class LocalRssPostSearchHighlights(
    val highlightedTitle: String?,
    val highlightedDescription: String?,
    val highlightedContent: String?
)
