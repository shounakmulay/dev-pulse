package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import org.koin.core.annotation.Factory

@Factory
class RssFeedSyncMetadataMapper(
    private val uuidMapper: UuidMapper
) {

    fun toLocalRssFeedSyncMetadata(from: RssFeedSyncMetadata) = LocalRssFeedSyncMetadata(
        feedId = uuidMapper.fromUuid(from.feedId),
        checkIntervalHours = from.checkIntervalHours,
        consecutiveEmptyFetches = from.consecutiveEmptyFetches,
        consecutiveFailures = from.consecutiveFailures,
        lastFetchedAt = from.lastFetchedAt,
        lastNewArticleAt = from.lastNewArticleAt,
        nextEligibleFetchAt = from.nextEligibleFetchAt,
    )

    fun toRssFeedSyncMetadata(from: LocalRssFeedSyncMetadata) = RssFeedSyncMetadata(
        feedId = uuidMapper.toUuid(from.feedId),
        checkIntervalHours = from.checkIntervalHours,
        consecutiveEmptyFetches = from.consecutiveEmptyFetches,
        consecutiveFailures = from.consecutiveFailures,
        lastFetchedAt = from.lastFetchedAt,
        lastNewArticleAt = from.lastNewArticleAt,
        nextEligibleFetchAt = from.nextEligibleFetchAt,
    )
}