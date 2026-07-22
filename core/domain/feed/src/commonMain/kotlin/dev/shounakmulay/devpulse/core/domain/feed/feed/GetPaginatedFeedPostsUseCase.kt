package dev.shounakmulay.devpulse.core.domain.feed.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSortOrder
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPaginatedFeedPostsUseCase(
    private val feedRepository: ContentFeedRepository,
) {

    operator fun invoke(
        feedId: String? = null,
        feedIds: Set<String> = emptySet(),
        sort: RssPostSort = RssPostSort.Published,
        order: RssPostSortOrder = RssPostSortOrder.Descending
    ): Flow<PagingData<RssPostWithFeedIdentity>> {
        val mergedFeedIds = buildSet {
            if (feedId != null) add(feedId)
            addAll(feedIds)
        }
        return feedRepository.getFeedPostsFlow(
            query = RssPostQuery(
                filters = listOf(RssPostFilter.FeedIds(mergedFeedIds)),
                sort = sort,
                order = order
            ),
            pagingConfig = PagingConfig(
                pageSize = 20,
            )
        )
    }
}
