package dev.shounakmulay.devpulse.core.data.db.model.feed

enum class LocalRssFeedQueueStatus(val dbValue: Int) {
    PROCESSING(dbValue = 1),
    QUEUED(dbValue = 2),
    COMPLETED(dbValue = 3),
    FAILED(dbValue = 4)
}
