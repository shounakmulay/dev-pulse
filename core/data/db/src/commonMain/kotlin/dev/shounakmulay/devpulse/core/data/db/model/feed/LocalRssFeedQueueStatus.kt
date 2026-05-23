package dev.shounakmulay.devpulse.core.data.db.model.feed

enum class LocalRssFeedQueueStatus() {
    PROCESSING,
    QUEUED,
    COMPLETED,
    FAILED
}
