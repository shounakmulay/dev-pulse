package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedPinnedFeedSourcesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SearchFeedsUseCase
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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
    fun getUIFeedsFlow(
        sources: Flow<FeedListSource>,
        searchQueries: Flow<String>,
    ): Flow<PagingData<UIFeed>> =
        combine(
            sources.distinctUntilChanged(),
            searchQueries.debouncedFeedSearchQueries(),
        ) { source, query -> source to query }
            .flatMapLatest { (source, query) ->
                when {
                    query.isNotBlank() -> feedSearchInteractor.getUIFeedSearchFlow(searchFeedsUseCase(query))
                    source == FeedListSource.Pinned -> feedInteractor.getUIFeedFlow(getPaginatedPinnedFeedSourcesUseCase())
                    else -> feedInteractor.getUIFeedFlow(getPaginatedFeedSourcesUseCase())
                }
            }
}

@OptIn(FlowPreview::class)
internal fun Flow<String>.debouncedFeedSearchQueries(): Flow<String> =
    map(String::trim)
        .distinctUntilChanged()
        .debounce { query -> if (query.length >= 3) 300L else 0L }
