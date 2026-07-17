package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.dao.FeedContentDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursorValue
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostSortTerm
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostSqlQueryBuilder
import dev.shounakmulay.devpulse.core.data.db.query.spec

class LocalRssPostWithFeedMetadataPagingDataProvider(
    private val feedContentDao: FeedContentDao,
    private val query: FeedPostQuery
) : LocalCursorPagingSourceDataProvider<
    FeedPostCursor,
    LocalRssPostWithFeedMetadataProjection
> {

    private val queryBuilder = FeedPostSqlQueryBuilder(query)
    private val spec = query.sort.spec

    override fun getTablesToTrack(): List<String> {
        return listOf(
            ContentFeedPostTableName,
            FeedTableName,
            PostCategoryTableName,
            PostTagMappingTableName,
            PostTagTableName
        )
    }

    override suspend fun getInitialPage(loadSize: Int): List<LocalRssPostWithFeedMetadataProjection> {
        return feedContentDao.getPostPage(queryBuilder.buildInitialPage(loadSize))
    }

    override suspend fun getPageAfter(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedMetadataProjection> {
        return feedContentDao.getPostPage(
            queryBuilder.buildPageAfter(
                cursor = cursor,
                limit = loadSize
            )
        )
    }

    override suspend fun getPageBefore(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedMetadataProjection> {
        return feedContentDao.getPostPage(
            queryBuilder.buildPageBefore(
                cursor = cursor,
                limit = loadSize
            )
        ).reversed()
    }

    override suspend fun getRefreshPageAround(
        anchorCursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedMetadataProjection> {
        val beforeLimit = loadSize / 2
        val afterLimit = loadSize - beforeLimit

        val beforeItems = getPageBefore(anchorCursor, beforeLimit)
        val afterItems = feedContentDao.getPostPage(
            queryBuilder.buildRefreshPageAround(
                cursor = anchorCursor,
                limit = afterLimit
            )
        )
        return beforeItems + afterItems
    }

    override fun getId(item: LocalRssPostWithFeedMetadataProjection): FeedPostCursor {
        val values = spec.terms.map { term -> term.extractCursorValue(item) }
        return FeedPostCursor(
            sort = query.sort,
            values = values
        )
    }

    private fun FeedPostSortTerm.extractCursorValue(
        item: LocalRssPostWithFeedMetadataProjection
    ): FeedPostCursorValue {
        return when (expression) {
            "p.publishedAtEpochMillis" -> FeedPostCursorValue.LongValue(item.post.publishedAtEpochMillis)
            "p.updatedAt" -> FeedPostCursorValue.LongValue(item.post.updatedAt)
            "p.createdAt" -> FeedPostCursorValue.LongValue(item.post.createdAt)
            "p.id" -> FeedPostCursorValue.TextValue(item.post.id)
            "p.title" -> FeedPostCursorValue.TextValue(item.post.title)
            "p.bookmarked" -> FeedPostCursorValue.BooleanValue(item.post.bookmarked)
            "f.name" -> FeedPostCursorValue.TextValue(item.feed.name.orEmpty())
            "f.pinned" -> FeedPostCursorValue.BooleanValue(item.feed.pinned)
            else -> error("Unknown sort expression: $expression")
        }
    }

    private companion object {
        const val ContentFeedPostTableName = "LocalRssContentFeedPost"
        const val FeedTableName = "LocalRssFeed"
        const val PostCategoryTableName = "LocalRssPostCategory"
        const val PostTagMappingTableName = "LocalRssPostToTagMapping"
        const val PostTagTableName = "LocalRssPostTag"
    }
}
