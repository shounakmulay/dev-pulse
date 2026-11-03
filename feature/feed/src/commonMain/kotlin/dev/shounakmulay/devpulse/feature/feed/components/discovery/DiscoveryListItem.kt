package dev.shounakmulay.devpulse.feature.feed.components.discovery

import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed

sealed interface DiscoveryListItem {
    data class Feed(val feed: UIDiscoveryFeed) : DiscoveryListItem
    data object EmptySearch : DiscoveryListItem
    data object EmptyCollection : DiscoveryListItem
}
