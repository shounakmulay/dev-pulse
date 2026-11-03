package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model

import dev.shounakmulay.devpulse.feature.feed.model.UIFeed

internal sealed interface FeedListItem {
    val key: String

    data class Feed(val feed: UIFeed) : FeedListItem {
        override val key: String get() = feed.id.value
    }

    sealed interface Empty : FeedListItem {
        override val key: String get() = "empty-feeds"

        data object All : Empty
        data object Pinned : Empty
        data object Search : Empty
    }
}
