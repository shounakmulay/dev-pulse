package dev.shounakmulay.devpulse.feature.feed.components.discovery

import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed

sealed interface DiscoveryListItem {
    val key: String

    data class Feed(val feed: UIDiscoveryFeed) : DiscoveryListItem {
        override val key: String get() = feed.id.value
    }

    sealed interface Empty : DiscoveryListItem {
        override val key: String get() = "empty-feeds"

        data object Search : Empty
        data object Collection : Empty
    }
}
