package dev.shounakmulay.devpulse.core.data.db.model.feed

enum class LocalRssFeedQueueStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}