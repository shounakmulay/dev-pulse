package dev.shounakmulay.devpulse.core.domain.models.feed

enum class RssFeedQueueActionType(val queueOrder: Int) {
    IMPORT(queueOrder = 1),
    SYNC(queueOrder = 2)
}
