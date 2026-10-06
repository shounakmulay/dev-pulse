package dev.shounakmulay.devpulse.feature.feed.components.feedOptions

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
sealed interface FeedOptionsMenuItem {
    @Stable
    @Serializable
    data class Pin(val pinned: Boolean) : FeedOptionsMenuItem

    @Stable
    @Serializable
    data class Share(val title: String, val sourceUrl: String) : FeedOptionsMenuItem

    @Stable
    @Serializable
    data class Delete(val title: String) : FeedOptionsMenuItem
}
