package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.events.DPEvents
import org.koin.core.annotation.Factory

@Factory
class DeleteFeedUseCase(
    private val feedRepository: FeedRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val dpEvent: DPEvent,
) {
    suspend operator fun invoke(id: UUID) = dispatcherProvider.runCatchingOnDefault {
        feedRepository.deleteFeed(id)
    }.onSuccess {
        dpEvent.track(DPEvents.FEED_DELETED)
    }
}
