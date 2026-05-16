package dev.shounakmulay.devpulse.core.data.feed

interface ContentFeedRepository {
    suspend fun parseRssFeed(url: String)
}