package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.common.extensions.mapToSuccessNotNull
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedQueuePagingDataUseCase(
    private val feedQueueRepository: RssFeedQueueRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(status: Set<RssFeedQueueStatus>) = feedQueueRepository
        .observeQueuePagingData(status)
        .flowCachingOnDefault(dispatcherProvider)
        .mapToSuccessNotNull()
}
