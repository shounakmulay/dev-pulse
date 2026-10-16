package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.dao.FeedDiscoveryFeedMetadataDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.data.db.query.FeedDiscoveryCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoverySqlQueryBuilder

internal class LocalFeedDiscoveryPagingDataProvider(
    private val dao: FeedDiscoveryFeedMetadataDao,
    private val query: LocalFeedDiscoveryQuery,
) : LocalCursorPagingSourceDataProvider<FeedDiscoveryCursor, LocalFeedDiscoveryFeed> {
    private val queryBuilder = LocalFeedDiscoverySqlQueryBuilder(query)

    override fun getTablesToTrack() = listOf(
        "LocalFeedDiscoveryFeedMetadata",
        "LocalFeedDiscoveryCollectionToFeedMapping",
        "LocalRssFeed",
        "LocalRssFeedQueue",
    )

    override suspend fun getInitialPage(loadSize: Int) =
        dao.getPage(queryBuilder.buildInitialPage(loadSize))

    override suspend fun getPageAfter(cursor: FeedDiscoveryCursor, loadSize: Int) =
        dao.getPage(queryBuilder.buildPageAfter(cursor, loadSize))

    override suspend fun getPageBefore(cursor: FeedDiscoveryCursor, loadSize: Int) =
        dao.getPage(queryBuilder.buildPageBefore(cursor, loadSize)).reversed()

    override suspend fun getRefreshPageAround(
        anchorCursor: FeedDiscoveryCursor,
        loadSize: Int,
    ): List<LocalFeedDiscoveryFeed> {
        val anchor = dao.getPage(queryBuilder.buildAnchorQuery(anchorCursor.id)).firstOrNull()
        val cursor = anchor?.let(::getId) ?: anchorCursor
        val before = getPageBefore(cursor, loadSize / 2)
        val after = dao.getPage(
            queryBuilder.buildRefreshPageAround(
                cursor = cursor,
                limit = loadSize - before.size,
            )
        )
        return before + after
    }

    override fun getId(item: LocalFeedDiscoveryFeed): FeedDiscoveryCursor = when (query) {
        is LocalFeedDiscoveryQuery.Collection -> FeedDiscoveryCursor.Collection(id = item.metadata.id, position = item.position)
        is LocalFeedDiscoveryQuery.Search -> FeedDiscoveryCursor.Search(id = item.metadata.id, rank = item.rank)
    }
}
