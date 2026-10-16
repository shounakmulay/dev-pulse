package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollection
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class DiscoveryFeedInteractor {
    suspend fun addFeed(feedId: UUID): Result<Unit> =
        Result.failure(UnsupportedOperationException("Discovery feed imports are not implemented for $feedId"))

    fun observeCollectionShelves(): Flow<List<UIDiscoveryCollectionShelf>> = flowOf(emptyList())

    fun observeCollection(collectionId: UUID): Flow<UIDiscoveryCollection?> = flowOf(null)

    fun collectionFeeds(
        collectionId: UUID,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<UIDiscoveryFeed>> = flowOf(PagingData.empty())

    fun observeSelectedFeed(feedIds: Flow<UUID?>): Flow<Pair<UUID?, UIDiscoveryFeed?>> =
        feedIds.map { id -> id to null }

    fun searchFeeds(
        queries: Flow<String>,
        pagingConfig: PagingConfig,
        collectionId: UUID? = null,
    ): Flow<PagingData<UIDiscoveryFeed>> = queries.map { PagingData.empty() }
}
