package dev.shounakmulay.devpulse.feature.feed.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIFeedSearchHighlights(
    val highlightedTitle: TextResource?,
    val highlightedName: TextResource?,
    val highlightedDescription: TextResource?,
)