package dev.shounakmulay.devpulse.core.data.feed.parser

internal interface FeedParser<out T> {
    suspend fun parseFeed(url: String): T
}

