package dev.shounakmulay.devpulse.core.domain.feed.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

import dev.shounakmulay.devpulse.core.data.feed.repository.FeedPostQueryIntent
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedPostSortIntent

@Factory
class GetPaginatedFeedPostsUseCase(
    private val feedRepository: ContentFeedRepository,
) {

    operator fun invoke(
        feedId: String? = null,
        feedIds: Set<String> = emptySet(),
        sort: FeedPostSortIntent = FeedPostSortIntent.PublishedNewest
    ): Flow<PagingData<RssPostWithFeedIdentity>> {
        val mergedFeedIds = buildSet {
            if (feedId != null) add(feedId)
            addAll(feedIds)
        }
        return feedRepository.getFeedPostsFlow(
            queryIntent = FeedPostQueryIntent(
                feedIds = mergedFeedIds,
                sort = sort
            ),
            pagingConfig = PagingConfig(
                pageSize = 20,
            )
        )
    }
}
