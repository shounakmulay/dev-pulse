package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.dao.FeedDiscoveryFeedMetadataDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeedPage
import dev.shounakmulay.devpulse.core.data.db.query.FeedDiscoveryCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery

internal class LocalFeedDiscoveryPagingDataProvider(
    private val dao: FeedDiscoveryFeedMetadataDao,
    private val query: LocalFeedDiscoveryQuery,
) : LocalCursorPagingSourceDataProvider<FeedDiscoveryCursor, LocalFeedDiscoveryFeedPage> {
    override fun getTablesToTrack() = listOf(
        "LocalFeedDiscoveryFeedMetadata",
        "LocalFeedDiscoveryCollectionToFeedMapping",
        "LocalRssFeed",
        "LocalRssFeedQueue",
    )

    override suspend fun getInitialPage(loadSize: Int) =
        getPageAfter(cursor = null, loadSize = loadSize)

    override suspend fun getPageAfter(cursor: FeedDiscoveryCursor, loadSize: Int) =
        getPageAfter(cursor = cursor, loadSize = loadSize, includeCursor = false)

    private suspend fun getPageAfter(
        cursor: FeedDiscoveryCursor?,
        loadSize: Int,
        includeCursor: Boolean = false,
    ): List<LocalFeedDiscoveryFeedPage> = when (query) {
        is LocalFeedDiscoveryQuery.Collection -> {
            val anchor = cursor as FeedDiscoveryCursor.Collection?
            dao.getCollectionPageAfter(
                collectionId = query.collectionId,
                afterPosition = anchor?.position,
                afterId = anchor?.id,
                includeCursor = includeCursor,
                limit = loadSize,
            )
        }
        is LocalFeedDiscoveryQuery.Search -> {
            val anchor = cursor as FeedDiscoveryCursor.Search?
            dao.getSearchPageAfter(
                query = query.text,
                collectionId = query.collectionId,
                afterRank = anchor?.rank,
                afterId = anchor?.id,
                includeCursor = includeCursor,
                limit = loadSize,
            )
        }
    }

    override suspend fun getPageBefore(cursor: FeedDiscoveryCursor, loadSize: Int): List<LocalFeedDiscoveryFeedPage> =
        when (query) {
            is LocalFeedDiscoveryQuery.Collection -> {
                val anchor = cursor as FeedDiscoveryCursor.Collection
                dao.getCollectionPageBefore(
                    collectionId = query.collectionId,
                    beforePosition = anchor.position,
                    beforeId = anchor.id,
                    limit = loadSize,
                )
            }
            is LocalFeedDiscoveryQuery.Search -> {
                val anchor = cursor as FeedDiscoveryCursor.Search
                dao.getSearchPageBefore(
                    query = query.text,
                    collectionId = query.collectionId,
                    beforeRank = anchor.rank,
                    beforeId = anchor.id,
                    limit = loadSize,
                )
            }
        }.reversed()

    override suspend fun getRefreshPageAround(
        anchorCursor: FeedDiscoveryCursor,
        loadSize: Int,
    ): List<LocalFeedDiscoveryFeedPage> {
        val cursor = when (query) {
            is LocalFeedDiscoveryQuery.Collection -> dao.getCollectionCursor(
                collectionId = query.collectionId,
                id = anchorCursor.id,
            )
            is LocalFeedDiscoveryQuery.Search -> dao.getSearchCursor(
                query = query.text,
                collectionId = query.collectionId,
                id = anchorCursor.id,
            )
        } ?: anchorCursor
        val before = getPageBefore(cursor, loadSize / 2)
        val after = getPageAfter(
            cursor = cursor,
            loadSize = loadSize - before.size,
            includeCursor = true,
        )
        return before + after
    }

    override fun getId(item: LocalFeedDiscoveryFeedPage): FeedDiscoveryCursor = when (item) {
        is LocalFeedDiscoveryFeedPage.Collection -> FeedDiscoveryCursor.Collection(
            id = item.feed.metadata.id,
            position = item.position,
        )
        is LocalFeedDiscoveryFeedPage.Search -> FeedDiscoveryCursor.Search(
            id = item.feed.metadata.id,
            rank = item.rank,
        )
    }
}
