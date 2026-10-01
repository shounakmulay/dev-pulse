package dev.shounakmulay.devpulse.feature.feed.model

import dev.shounakmulay.devpulse.core.ui.text.TextResource
import kotlinx.serialization.Serializable

@Serializable
data class UIFeedPostSearchHighlights(
    val highlightedTitle: TextResource?,
    val highlightedDescription: TextResource?,
    val highlightedContent: TextResource?
)