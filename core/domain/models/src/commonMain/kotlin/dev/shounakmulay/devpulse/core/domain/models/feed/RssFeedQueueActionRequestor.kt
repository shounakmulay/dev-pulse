package dev.shounakmulay.devpulse.core.domain.models.feed

enum class RssFeedQueueActionRequestor(val queueOrder: Int) {
    USER(queueOrder = 1),
    APP(queueOrder = 2)
}
