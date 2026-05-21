package dev.shounakmulay.devpulse.core.domain.feed

import org.koin.core.annotation.Factory

@Factory
class InitialiseFeedQueueProcessingUseCase(
    private val queueExecutor: RssFeedQueueExecutor
) {
    operator fun invoke() {
        queueExecutor.processQueue()
    }
}