package dev.shounakmulay.devpulse.core.domain.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import org.koin.core.annotation.Factory

@Factory
class ImportFeedUseCase(
    private val rssFeedQueueRepository: RssFeedQueueRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val dateTimeProvider: DateTimeProvider
) {
    suspend operator fun invoke(url: String, name: String?, type: RssFeedType) =
        dispatcherProvider.runCatchingOnDefault {
            val now = dateTimeProvider.now()
            val queueEntry = RssFeedQueueEntry(
                url = url,
                name = name,
                feedType = type,
                actionType = RssFeedQueueActionType.IMPORT,
                requestor = RssFeedQueueActionRequestor.USER,
                status = RssFeedQueueStatus.QUEUED,
                createdAt = now,
                updatedAt = now,
            )
            rssFeedQueueRepository.enqueue(queueEntry)
        }
}