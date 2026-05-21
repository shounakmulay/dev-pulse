package dev.shounakmulay.devpulse.core.data.db.model.feed

enum class LocalRssFeedQueueActionType(val dbValue: Int) {
    IMPORT(dbValue = 1),
    SYNC(dbValue = 2)
}
