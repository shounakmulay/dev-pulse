package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

interface RssFeedQueueExecutor {
    fun triggerQueueProcessing()
    suspend fun process(): RssFeedQueueProcessingResult
    fun isProcessing(): Boolean
}

data class RssFeedQueueProcessingResult(
    val syncedFeedIds: Set<UUID> = emptySet(),
    val feedsWithNewPosts: Set<UUID> = emptySet(),
)
