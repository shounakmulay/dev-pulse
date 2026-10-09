package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

interface RssFeedQueueExecutor {
    fun triggerQueueProcessing()
    suspend fun process(): Set<UUID>
    fun isProcessing(): Boolean
}