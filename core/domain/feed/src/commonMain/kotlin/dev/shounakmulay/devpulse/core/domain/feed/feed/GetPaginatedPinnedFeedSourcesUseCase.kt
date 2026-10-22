package dev.shounakmulay.devpulse.core.domain.feed.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithUserState
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPaginatedPinnedFeedSourcesUseCase(
    private val feedRepository: FeedRepository,
) {

    operator fun invoke(): Flow<PagingData<RssFeedWithUserState>> {
        return feedRepository.getPinnedFeedFlow(
            pagingConfig = PagingConfig(
                pageSize = 10,
            )
        )
    }
}