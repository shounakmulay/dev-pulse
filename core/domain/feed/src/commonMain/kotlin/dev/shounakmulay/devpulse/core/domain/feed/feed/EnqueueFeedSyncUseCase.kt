package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedSyncRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.feed.queue.RssFeedQueueExecutor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import org.koin.core.annotation.Factory

@Factory
class EnqueueFeedSyncUseCase(
    private val rssFeedQueueRepository: RssFeedQueueRepository,
    private val feedSyncRepository: FeedSyncRepository,
    private val queueExecutor: RssFeedQueueExecutor,
    private val dispatcherProvider: DispatcherProvider,
    private val dateTimeProvider: DateTimeProvider,
) {

    suspend operator fun invoke() = dispatcherProvider.runCatchingOnDefault {
        val feeds = feedSyncRepository.getFeedsDueForSync()
        val entries = feeds.map { (metadata, identity) ->
            val now = dateTimeProvider.now().toEpochMilliseconds()
            RssFeedQueueEntry(
                url = identity.sourceUrl,
                feedId = identity.id,
                name = identity.name,
                feedType = RssFeedType.CONTENT,
                actionType = RssFeedQueueActionType.SYNC,
                requestor = RssFeedQueueActionRequestor.USER,
                status = RssFeedQueueStatus.QUEUED,
                createdAt = now,
                updatedAt = now,
            )
        }
        rssFeedQueueRepository.enqueue(entries)
        queueExecutor.processQueue()
    }
}