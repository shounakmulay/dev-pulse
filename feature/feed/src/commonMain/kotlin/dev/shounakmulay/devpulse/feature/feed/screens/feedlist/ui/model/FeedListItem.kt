package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model

import dev.shounakmulay.devpulse.feature.feed.model.UIFeed

internal sealed interface FeedListItem {
    data class Feed(val feed: UIFeed) : FeedListItem

    sealed interface Empty : FeedListItem {
        data object All : Empty
        data object Pinned : Empty
        data object Search : Empty
    }
}
