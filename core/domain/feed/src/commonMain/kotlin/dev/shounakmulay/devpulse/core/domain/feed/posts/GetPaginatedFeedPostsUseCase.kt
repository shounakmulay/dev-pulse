package dev.shounakmulay.devpulse.core.domain.feed.posts

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPaginatedFeedPostsUseCase(
    private val postRepository: PostRepository,
) {

    operator fun invoke(
        filters: List<RssPostFilter>,
        sort: RssPostSort = RssPostSort.PublishedNewest,
    ): Flow<PagingData<RssPostWithFeedIdentityAndSearch>> {
        return postRepository.observePosts(
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
