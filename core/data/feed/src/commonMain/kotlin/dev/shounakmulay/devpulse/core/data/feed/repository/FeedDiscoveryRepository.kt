package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryFeed
import kotlinx.coroutines.flow.Flow

interface FeedDiscoveryRepository {
    suspend fun isFeedDiscoveryInitialised(): Boolean
    suspend fun getDiscoverySeed(seedFilePath: String): DiscoverySeedData
    suspend fun initialiseWithSeed(seed: DiscoverySeedData)
    suspend fun refresh()

    fun observeCollectionShelves(previewCount: Int): Flow<List<FeedDiscoveryCollectionShelf>>
    fun observeCollection(collectionId: UUID): Flow<FeedDiscoveryCollection?>
    fun observeFeed(feedId: UUID): Flow<FeedDiscoveryFeed?>
    fun observeFeeds(query: FeedDiscoveryQuery, pagingConfig: PagingConfig): Flow<PagingData<FeedDiscoveryFeed>>
}
