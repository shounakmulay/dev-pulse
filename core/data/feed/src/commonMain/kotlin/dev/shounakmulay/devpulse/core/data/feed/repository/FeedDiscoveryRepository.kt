package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.feed.model.discoveryseed.DiscoverySeedData

interface FeedDiscoveryRepository {
    suspend fun isFeedDiscoveryInitialised(): Boolean
    suspend fun getDiscoverySeed(seedFilePath: String): DiscoverySeedData
    suspend fun initialiseWithSeed(seed: DiscoverySeedData)
    suspend fun refresh()
}