package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursorValue
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import dev.shounakmulay.devpulse.core.data.db.query.spec

object FeedPostPagingFixtures {

    private val duplicateSortRows = listOf(
        projection(
            id = "post-004",
            feedId = "feed-beta",
            title = "Compose",
            feedTitle = "Beta",
            feedName = "Beta",
            publishedAtEpochMillis = 200L,
            createdAt = 40L,
            updatedAt = 50L
        ),
        projection(
            id = "post-001",
            feedId = "feed-alpha",
            title = "",
            feedTitle = "Alpha",
            feedName = null,
            publishedAtEpochMillis = Long.MIN_VALUE,
            createdAt = 10L,
            updatedAt = 10L
        ),
        projection(
            id = "post-006",
            feedId = "feed-gamma",
            title = "Kotlin",
            feedTitle = "Gamma",
            feedName = null,
            publishedAtEpochMillis = 300L,
            createdAt = 60L,
            updatedAt = 60L
        ),
        projection(
            id = "post-002",
            feedId = "feed-alpha",
            title = "Architecture",
            feedTitle = "Alpha",
            feedName = "Alpha",
            publishedAtEpochMillis = 100L,
            createdAt = 20L,
            updatedAt = 20L,
            bookmarked = true
        ),
        projection(
            id = "post-005",
            feedId = "feed-beta",
            title = "Compose",
            feedTitle = "Beta",
            feedName = "Beta",
            publishedAtEpochMillis = 200L,
            createdAt = 50L,
            updatedAt = 50L
        ),
        projection(
            id = "post-003",
            feedId = "feed-alpha",
            title = "Architecture",
            feedTitle = "Alpha",
            feedName = "Alpha",
            publishedAtEpochMillis = 200L,
            createdAt = 40L,
            updatedAt = 40L,
            bookmarked = true
        )
    )

    val publishedNewest = FeedPostPagingDataset(
        query = LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest),
        pageSize = 3,
        insertedRows = duplicateSortRows,
        expectedIds = listOf(
            "post-006",
            "post-005",
            "post-004",
            "post-003",
            "post-002",
            "post-001"
        )
    )

    val publishedOldest = FeedPostPagingDataset(
        query = LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedOldest),
        pageSize = 4,
        insertedRows = duplicateSortRows,
        expectedIds = listOf(
            "post-001",
            "post-002",
            "post-003",
            "post-004",
            "post-005",
            "post-006"
        )
    )

    val updatedNewest = FeedPostPagingDataset(
        query = LocalFeedPostQuery(sort = LocalFeedPostSort.UpdatedNewest),
        pageSize = 2,
        insertedRows = duplicateSortRows,
        expectedIds = listOf(
            "post-006",
            "post-005",
            "post-004",
            "post-003",
            "post-002",
            "post-001"
        )
    )

    val createdOldest = FeedPostPagingDataset(
        query = LocalFeedPostQuery(sort = LocalFeedPostSort.CreatedOldest),
        pageSize = 3,
        insertedRows = duplicateSortRows,
        expectedIds = listOf(
            "post-001",
            "post-002",
            "post-003",
            "post-004",
            "post-005",
            "post-006"
        )
    )

    val titleAtoZ = FeedPostPagingDataset(
        query = LocalFeedPostQuery(sort = LocalFeedPostSort.TitleAtoZ),
        pageSize = 2,
        insertedRows = duplicateSortRows,
        expectedIds = listOf(
            "post-001",
            "post-002",
            "post-003",
            "post-004",
            "post-005",
            "post-006"
        )
    )

    val feedNameAtoZ = FeedPostPagingDataset(
        query = LocalFeedPostQuery(sort = LocalFeedPostSort.FeedNameAtoZ),
        pageSize = 2,
        insertedRows = duplicateSortRows,
        expectedIds = listOf(
            "post-001",
            "post-002",
            "post-003",
            "post-004",
            "post-005",
            "post-006"
        )
    )

    val duplicateBoundaryDatasets = listOf(
        publishedNewest,
        publishedOldest,
        updatedNewest,
        createdOldest,
        titleAtoZ,
        feedNameAtoZ
    )

    fun postIds(
        rows: List<LocalRssPostWithFeedAndSearch>
    ): List<String> {
        return rows.map { it.post.id }
    }

    fun projection(
        id: String,
        feedId: String,
        title: String,
        feedTitle: String?,
        feedName: String?,
        publishedAtEpochMillis: Long,
        createdAt: Long,
        updatedAt: Long,
        bookmarked: Boolean = false,
        pinned: Boolean = false
    ): LocalRssPostWithFeedAndSearch {
        return LocalRssPostWithFeedAndSearch(
            post = LocalRssContentFeedPost(
                id = id,
                feedId = feedId,
                fingerprint = "$id-fingerprint",
                guid = "$id-guid",
                title = title,
                author = "Author $id",
                link = "https://example.com/posts/$id",
                pubDate = null,
                publishedAtEpochMillis = publishedAtEpochMillis,
                description = "Description $id",
                content = "Content $id",
                image = null,
                audio = null,
                video = null,
                sourceName = feedName ?: feedTitle ?: "",
                sourceUrl = "https://example.com/feeds/$feedId.xml",
                categories = "",
                commentsUrl = null,
                bookmarked = bookmarked,
                youtubeData = null,
                rawEnclosure = null,
                rawMedia = null,
                createdAt = createdAt,
                updatedAt = updatedAt
            ),
            feed = LocalRssFeedIdentitySlice(
                id = feedId,
                title = feedTitle,
                name = feedName,
                pinned = pinned,
                sourceUrl = "https://example.com/feeds/$feedId.xml",
                link = "https://example.com/feeds/$feedId",
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        )
    }
}

data class FeedPostPagingDataset(
    val query: LocalFeedPostQuery,
    val pageSize: Int,
    val insertedRows: List<LocalRssPostWithFeedAndSearch>,
    val expectedIds: List<String>
) {

    val expectedRows: List<LocalRssPostWithFeedAndSearch> = expectedIds.map { row(it) }

    val expectedPageIds: List<List<String>> = expectedIds.chunked(pageSize)

    fun cursorAfterPage(pageIndex: Int): FeedPostCursor {
        return cursorFor(row(expectedPageIds[pageIndex].last()))
    }

    fun cursorFor(row: LocalRssPostWithFeedAndSearch): FeedPostCursor {
        val spec = query.sort.spec
        val values = spec.terms.map { term ->
            when (term.expression) {
                "p.publishedAtEpochMillis" -> FeedPostCursorValue.LongValue(row.post.publishedAtEpochMillis)
                "p.updatedAt" -> FeedPostCursorValue.LongValue(row.post.updatedAt)
                "p.createdAt" -> FeedPostCursorValue.LongValue(row.post.createdAt)
                "p.id" -> FeedPostCursorValue.TextValue(row.post.id)
                "p.title" -> FeedPostCursorValue.TextValue(row.post.title)
                "p.bookmarked" -> FeedPostCursorValue.BooleanValue(row.post.bookmarked)
                "f.name" -> FeedPostCursorValue.TextValue(row.feed.name.orEmpty())
                "f.pinned" -> FeedPostCursorValue.BooleanValue(row.feed.pinned)
                else -> error("Unknown sort expression: ${term.expression}")
            }
        }
        return FeedPostCursor(sort = query.sort, values = values)
    }

    fun row(id: String): LocalRssPostWithFeedAndSearch {
        return insertedRows.first { it.post.id == id }
    }
}
