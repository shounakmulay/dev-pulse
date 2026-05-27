package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import org.koin.core.annotation.Factory

@Factory
class SetFeedPinnedUseCase(
    private val feedRepository: ContentFeedRepository,
) {
    suspend operator fun invoke(id: String, pinned: Boolean): Result<Unit> {
        return feedRepository.setFeedPinned(id = id, pinned = pinned)
    }
}
