package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import org.koin.core.annotation.Factory

@Factory
class SetFeedPinnedUseCase(
    private val feedRepository: FeedRepository,
) {
    suspend operator fun invoke(id: UUID, pinned: Boolean): Result<Unit> {
        return feedRepository.setFeedPinned(id = id, pinned = pinned)
    }
}
