package dev.shounakmulay.devpulse.core.domain.feed.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithSearch
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPaginatedFeedSourcesUseCase(
    private val feedRepository: FeedRepository,
) {

    operator fun invoke(searchQuery: String? = null, pinnedOnly: Boolean = false): Flow<PagingData<RssFeedWithSearch>> {
        return feedRepository.getFeedsListFlow(
            searchQuery = searchQuery,
            pinnedOnly = pinnedOnly,
            pagingConfig = PagingConfig(
                pageSize = 10,
            )
        )
    }
}
