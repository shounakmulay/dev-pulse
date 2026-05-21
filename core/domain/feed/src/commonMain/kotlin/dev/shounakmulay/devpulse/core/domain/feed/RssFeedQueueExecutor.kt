package dev.shounakmulay.devpulse.core.domain.feed

interface RssFeedQueueExecutor {
    fun processQueue()
    fun isProcessing(): Boolean
}