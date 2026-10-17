package dev.shounakmulay.devpulse.core.domain.feed.discovery

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedDiscoveryRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.EnqueueFeedImportsUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.AddFeedData
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryImportState
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class EnqueueFeedDiscoveryImportUseCase(
    private val feedDiscoveryRepository: FeedDiscoveryRepository,
    private val enqueueFeedImportsUseCase: EnqueueFeedImportsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(feedId: UUID): Result<FeedDiscoveryImportState> =
        dispatcherProvider.runCatchingOnDefault {
            val feed = requireNotNull(feedDiscoveryRepository.observeFeed(feedId).first())
            when (feed.importState) {
                FeedDiscoveryImportState.Added,
                FeedDiscoveryImportState.Adding,
                FeedDiscoveryImportState.Queued -> feed.importState
                FeedDiscoveryImportState.Failed,
                FeedDiscoveryImportState.NotAdded -> {
                    enqueueFeedImportsUseCase(
                        listOf(
                            AddFeedData(
                                url = feed.url,
                                name = feed.title,
                                type = RssFeedType.CONTENT,
                                tags = emptyList(),
                                folders = emptyList(),
                            )
                        )
                    ).getOrThrow()
                    FeedDiscoveryImportState.Queued
                }
            }
        }
}
