package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.logging.DPLogger
import org.koin.core.annotation.Factory

@Factory
class InitialiseFeedQueueProcessingUseCase(
    private val queueExecutor: RssFeedQueueExecutor,
    logger: DPLogger
) {
    private val logger = logger.withTag(Tag)

    operator fun invoke() {
        logger.i { "Initialising feed queue processing" }
        queueExecutor.processQueue()
    }

    private companion object {
        const val Tag = "RssFeedQueueExecutor"
    }
}
