package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.data.db.query.FeedDiscoveryCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery

interface FeedDiscoveryPagingSourceProvider {
    fun getFeedDiscoveryPagingSource(
        query: LocalFeedDiscoveryQuery,
    ): PagingSource<FeedDiscoveryCursor, LocalFeedDiscoveryFeed>
}
