package dev.shounakmulay.devpulse.core.data.feed.parser

internal interface FeedParser<T> {
    suspend fun parseFeed(url: String): T
}
