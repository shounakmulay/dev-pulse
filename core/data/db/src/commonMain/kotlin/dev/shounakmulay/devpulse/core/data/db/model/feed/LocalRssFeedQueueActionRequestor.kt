package dev.shounakmulay.devpulse.core.data.db.model.feed

enum class LocalRssFeedQueueActionRequestor(val dbValue: Int) {
    USER(dbValue = 1),
    APP(dbValue = 2)
}
