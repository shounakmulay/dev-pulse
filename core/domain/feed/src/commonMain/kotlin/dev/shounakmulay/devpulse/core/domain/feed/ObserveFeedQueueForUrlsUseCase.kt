package dev.shounakmulay.devpulse.core.domain.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.common.extensions.mapToSuccessNotNull
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedQueueForUrlsUseCase(
    private val feedQueueRepository: RssFeedQueueRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    operator fun invoke(urls: List<String>) = feedQueueRepository
        .observeQueueForUrls(urls)
        .flowCachingOnDefault(dispatcherProvider)
        .mapToSuccessNotNull()
}