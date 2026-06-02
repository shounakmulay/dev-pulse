package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry

internal interface FeedImportCandidate {
    val id: String

    suspend fun import(entry: RssFeedQueueEntry)
}
