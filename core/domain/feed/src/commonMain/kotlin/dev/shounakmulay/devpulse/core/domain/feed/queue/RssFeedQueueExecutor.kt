package dev.shounakmulay.devpulse.core.domain.feed.queue

interface RssFeedQueueExecutor {
    fun processQueue()
    fun isProcessing(): Boolean
}