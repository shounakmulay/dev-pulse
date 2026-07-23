package dev.shounakmulay.devpulse.core.domain.feed.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPaginatedFeedPostsUseCase(
    private val feedRepository: ContentFeedRepository,
) {

    operator fun invoke(
        filters: List<RssPostFilter>,
        sort: RssPostSort = RssPostSort.PublishedNewest,
    ): Flow<PagingData<RssPostWithFeedIdentity>> {
        return feedRepository.getFeedPostsFlow(
            query = RssPostQuery(
                filters = filters,
                sort = sort,
            ),
            pagingConfig = PagingConfig(
                pageSize = 20,
            )
        )
    }
}
