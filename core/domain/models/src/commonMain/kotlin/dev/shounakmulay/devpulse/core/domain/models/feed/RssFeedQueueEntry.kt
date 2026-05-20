package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeedQueueEntry(
    val id: Int = 0,
    val url: String,
    val name: String?,
    val feedType: RssFeedType,
    val actionType: RssFeedQueueActionType,
    val requestor: RssFeedQueueActionRequestor,
    val status: RssFeedQueueStatus,
    val fetchAttempt: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
