package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry

internal interface FeedImportCandidate {
    val id: String

    suspend fun parse(entry: RssFeedQueueEntry, xml: String) : ParsedFeed
    suspend fun parse(entry: RssFeedQueueEntry, iterator: CharIterator): ParsedFeed
}
