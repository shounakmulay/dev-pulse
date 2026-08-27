package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedSyncMetadataDao
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedSyncMetadataMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import org.koin.core.annotation.Factory

@Factory
class FeedSyncMetadataRepositoryImpl(
    private val syncMetadataDao: FeedSyncMetadataDao,
    private val syncMetadataMapper: RssFeedSyncMetadataMapper,
    private val feedMapper: RssFeedMapper,
    private val uuidMapper: UuidMapper,
    private val dateTimeProvider: DateTimeProvider,
) : FeedSyncMetadataRepository {
    override suspend fun getSyncMetadata(id: UUID): RssFeedSyncMetadata? {
        val localMetadata = syncMetadataDao.getSyncMetadata(uuidMapper.fromUuid(id))
        return localMetadata?.let { syncMetadataMapper.toRssFeedSyncMetadata(it) }
    }

    override suspend fun getSyncMetadata(ids: List<UUID>): Map<UUID, RssFeedSyncMetadata> {
        return syncMetadataDao.getSyncMetadata(ids.map { uuidMapper.fromUuid(it) })
            .associateBy { it.feedId }
            .map { (localId, localMetadata) ->
                uuidMapper.toUuid(localId) to syncMetadataMapper.toRssFeedSyncMetadata(localMetadata)
            }
            .toMap()
    }

    override suspend fun upsertSyncMetadata(metadata: RssFeedSyncMetadata) {
        syncMetadataDao.upsertSyncMetadata(
            listOf(
                syncMetadataMapper.toLocalRssFeedSyncMetadata(
                    metadata
                )
            )
        )
    }

    override suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>> {
        val now = dateTimeProvider.nowEpochMilliseconds()
        return syncMetadataDao.getFeedsPastNextEligibleFetch(now)
            .map {
                syncMetadataMapper.toRssFeedSyncMetadata(it.syncMetadata) to feedMapper.toRssIdentity(
                    it.identitySlice
                )
            }
    }
}