package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import org.koin.core.annotation.Factory

@Factory
class SetPostBookmarkedUseCase(
    private val feedRepository: ContentFeedRepository,
) {
    suspend operator fun invoke(id: String, bookmarked: Boolean): Result<Unit> {
        return feedRepository.setPostBookmarked(id = id, bookmarked = bookmarked)
    }
}
