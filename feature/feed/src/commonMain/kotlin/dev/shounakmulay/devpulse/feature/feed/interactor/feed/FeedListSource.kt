package dev.shounakmulay.devpulse.feature.feed.interactor.feed

sealed interface FeedListSource {
    data object All : FeedListSource
    data object Pinned : FeedListSource
}
