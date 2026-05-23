package dev.shounakmulay.devpulse.core.domain.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.AddFeedData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.logging.DPLogger
import org.koin.core.annotation.Factory

@Factory
class ImportFeedUseCase(
    private val rssFeedQueueRepository: RssFeedQueueRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val dateTimeProvider: DateTimeProvider,
    private val queueExecutor: RssFeedQueueExecutor,
    logger: DPLogger
) {
    private val logger = logger.withTag(Tag)

    suspend operator fun invoke(feeds: List<AddFeedData>) =
        dispatcherProvider.runCatchingOnDefault {
            val entries = feeds.map { addFeedData ->
                val now = dateTimeProvider.now()
                val queueEntry = RssFeedQueueEntry(
                    url = addFeedData.url,
                    name = addFeedData.name,
                    feedType = addFeedData.type,
                    actionType = RssFeedQueueActionType.IMPORT,
                    requestor = RssFeedQueueActionRequestor.USER,
                    status = RssFeedQueueStatus.QUEUED,
                    createdAt = now,
                    updatedAt = now,
                    tags = addFeedData.tags,
                    folders = addFeedData.folders,
                )
                logger.d { queueEntry.importLogMessage() }
                queueEntry
            }
            rssFeedQueueRepository.enqueue(entries)
            queueExecutor.processQueue()
        }

    private fun RssFeedQueueEntry.importLogMessage(): String {
        return "Queue import requested action=$actionType requestor=$requestor " +
                "feedType=$feedType hasName=${name != null} source=${url.sourceSummary()}"
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host =
            withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "ImportFeedUseCase"
    }
}
