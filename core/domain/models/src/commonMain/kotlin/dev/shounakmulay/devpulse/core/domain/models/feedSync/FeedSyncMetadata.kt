package dev.shounakmulay.devpulse.core.domain.models.feedSync

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedSyncMetadata(
    val feedId: UUID,
    val checkIntervalHours: Int = 1,
    val consecutiveEmptyFetches: Int = 0,
    val consecutiveFailures: Int = 0,
    val lastFetchedAt: Long,
    val lastNewArticleAt: Long?,
    val nextEligibleFetchAt: Long,
    val etag: String? = null,
    val lastModified: String? = null
)