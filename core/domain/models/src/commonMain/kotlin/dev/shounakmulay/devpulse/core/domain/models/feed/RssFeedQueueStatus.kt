package dev.shounakmulay.devpulse.core.domain.models.feed

enum class RssFeedQueueStatus(val queueOrder: Int) {
    PROCESSING(queueOrder = 1),
    QUEUED(queueOrder = 2),
    COMPLETED(queueOrder = 3),
    FAILED(queueOrder = 4)
}
