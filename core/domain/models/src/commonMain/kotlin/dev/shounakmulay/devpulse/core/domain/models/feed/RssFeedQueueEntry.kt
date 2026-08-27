package dev.shounakmulay.devpulse.core.domain.models.feed

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedQueueEntry(
    val id: Int = 0,
    val feedId: UUID?,
    val url: String,
    val name: String?,
    val feedType: RssFeedType,
    val actionType: RssFeedQueueActionType,
    val requestor: RssFeedQueueActionRequestor,
    val status: RssFeedQueueStatus,
    val tags: List<Int> = emptyList(),
    val folders: List<Int> = emptyList(),
    val fetchAttempt: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
