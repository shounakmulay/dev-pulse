package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedPinnedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SearchFeedsUseCase
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.annotation.Factory

@Factory
class FeedListInteractor(
    private val getPaginatedFeedSourcesUseCase: GetPaginatedFeedSourcesUseCase,
    private val getPaginatedPinnedFeedSourcesUseCase: GetPaginatedPinnedFeedSourcesUseCase,
    private val searchFeedsUseCase: SearchFeedsUseCase,
    private val feedInteractor: FeedInteractor,
    private val feedSearchInteractor: FeedSearchInteractor,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUIFeedsFlow(sources: Flow<FeedListSource>): Flow<PagingData<UIFeed>> =
        sources.distinctUntilChanged().flatMapLatest { source ->
            when (source) {
                FeedListSource.All -> feedInteractor.getUIFeedFlow(getPaginatedFeedSourcesUseCase())
                FeedListSource.Pinned -> feedInteractor.getUIFeedFlow(getPaginatedPinnedFeedSourcesUseCase())
                is FeedListSource.Search -> {
                    feedSearchInteractor.getUIFeedSearchFlow(searchFeedsUseCase(source.query))
                }
            }
        }
}
