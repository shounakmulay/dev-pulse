package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed

internal interface FeedParser {
    suspend fun parseFeed(url: String): ParsedFeed

    fun parseText(sourceUrl: String?, xml: String): ParsedFeed
}
