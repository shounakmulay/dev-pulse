package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata

interface FeedSyncMetadataRepository {
    suspend fun getSyncMetadata(id: UUID): RssFeedSyncMetadata?
    suspend fun getSyncMetadata(ids: List<UUID>): Map<UUID, RssFeedSyncMetadata>
    suspend fun upsertSyncMetadata(metadata: RssFeedSyncMetadata)
    suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>>
}