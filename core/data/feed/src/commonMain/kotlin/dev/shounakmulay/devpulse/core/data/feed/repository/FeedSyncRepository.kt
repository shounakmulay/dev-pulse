package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed

interface FeedSyncRepository {
    suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>>
    suspend fun importPosts(postsWithExistingIdentities: List<RssFeedPostWithExistingIdentity>)
    suspend fun importFeed(
        entry: RssFeedQueueEntry,
        parsedFeed: ParsedFeed,
        existingIdentity: RssFeedIdentity?
    ): RssFeed

    suspend fun onImportCompleted(
        entry: RssFeedQueueEntry,
        feed: RssFeed,
        savedCount: Int,
        metadata: RssFeedSyncMetadata,
        removeStaleEntries: Boolean = true
    )

    suspend fun onImportFailed(
        entry: RssFeedQueueEntry,
        metadata: RssFeedSyncMetadata?
    )

    suspend fun removeStaleEntries(entry: RssFeedQueueEntry)
}