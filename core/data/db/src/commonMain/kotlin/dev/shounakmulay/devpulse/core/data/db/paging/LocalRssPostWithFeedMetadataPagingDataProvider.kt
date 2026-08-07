package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSqlQueryBuilder
import dev.shounakmulay.devpulse.core.data.db.query.SqlBinding
import dev.shounakmulay.devpulse.core.logging.DPLog

class LocalRssPostWithFeedMetadataPagingDataProvider(
    private val feedPostDao: FeedPostDao,
    private val query: LocalFeedPostQuery
) : LocalCursorPagingSourceDataProvider<
        FeedPostCursor,
        LocalRssPostWithFeedMetadataProjection
        > {

    private val queryBuilder = LocalFeedPostSqlQueryBuilder(query)
    private val logger = DPLog.tag(Tag)

    override fun getTablesToTrack(): List<String> {
        val tables = listOf(
            ContentFeedPostTableName,
            FeedTableName,
            PostCategoryTableName,
            PostTagMappingTableName,
            PostTagTableName
        )
        logger.v { "[FEED-PAGING] tablesToTrack=${tables.joinToString()}" }
        return tables
    }

    override suspend fun getInitialPage(loadSize: Int): List<LocalRssPostWithFeedMetadataProjection> {
        logger.d {
            "[FEED-PAGING] initial:start loadSize=$loadSize sort=${query.sort} " +
                    "filters=${query.filters}"
        }
        val items = feedPostDao.getPostPage(queryBuilder.buildInitialPage(loadSize))
        logger.d { "[FEED-PAGING] initial:end ${items.summary()}" }
        return items
    }

    override suspend fun getPageAfter(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedMetadataProjection> {
        logger.d {
            "[FEED-PAGING] append:start loadSize=$loadSize cursor=${cursor.summary()}"
        }
        val items = feedPostDao.getPostPage(
            queryBuilder.buildPageAfter(
                cursor = cursor,
                limit = loadSize
            )
        )
        logger.d { "[FEED-PAGING] append:end ${items.summary()}" }
        return items
    }

    override suspend fun getPageBefore(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedMetadataProjection> {
        logger.d {
            "[FEED-PAGING] prepend:start loadSize=$loadSize cursor=${cursor.summary()}"
        }
        val items = feedPostDao.getPostPage(
            queryBuilder.buildPageBefore(
                cursor = cursor,
                limit = loadSize
            )
        ).reversed()
        logger.d { "[FEED-PAGING] prepend:end ${items.summary()}" }
        return items
    }

    override suspend fun getRefreshPageAround(
        anchorCursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedMetadataProjection> {
        val beforeLimit = loadSize / 2
        val afterLimit = loadSize - beforeLimit

        logger.d {
            "[FEED-PAGING] refreshAround:start loadSize=$loadSize beforeLimit=$beforeLimit " +
                    "afterLimit=$afterLimit anchor=${anchorCursor.summary()}"
        }
        val beforeItems = getPageBefore(anchorCursor, beforeLimit)
        val afterItems = feedPostDao.getPostPage(
            queryBuilder.buildRefreshPageAround(
                cursor = anchorCursor,
                limit = afterLimit
            )
        )
        val items = beforeItems + afterItems
        logger.d {
            "[FEED-PAGING] refreshAround:end before=${beforeItems.summary()} " +
                    "after=${afterItems.summary()} combined=${items.summary()}"
        }
        return items
    }

    override fun getId(item: LocalRssPostWithFeedMetadataProjection): FeedPostCursor {
        val cursor = FeedPostCursor(
            id = item.post.id,
            sort = query.sort,
            sortValue = query.sort.extractCursorValue(item),
        )
        logger.v { "[FEED-PAGING] cursorFromItem itemId=${item.post.id} cursor=${cursor.summary()}" }
        return cursor
    }

    private fun LocalFeedPostSort.extractCursorValue(
        item: LocalRssPostWithFeedMetadataProjection
    ): SqlBinding {
        return when (this) {
            LocalFeedPostSort.PublishedNewest,
            LocalFeedPostSort.PublishedOldest -> SqlBinding.LongValue(item.post.publishedAtEpochMillis)

            LocalFeedPostSort.TitleAtoZ,
            LocalFeedPostSort.TitleZtoA -> SqlBinding.Text(item.post.title)
        }
    }

    private fun FeedPostCursor.summary(): String {
        return "{id=$id sort=$sort sortValue=$sortValue}"
    }

    private fun List<LocalRssPostWithFeedMetadataProjection>.summary(): String {
        return "count=$size firstId=${firstOrNull()?.post?.id} lastId=${lastOrNull()?.post?.id} " +
                "ids=${joinToString(prefix = "[", postfix = "]") { it.post.id.value }}"
    }

    private companion object {
        const val Tag = "FeedPostPagingProvider"
        const val ContentFeedPostTableName = "LocalRssContentFeedPost"
        const val FeedTableName = "LocalRssFeed"
        const val PostCategoryTableName = "LocalRssPostCategory"
        const val PostTagMappingTableName = "LocalRssPostToTagMapping"
        const val PostTagTableName = "LocalRssPostTag"
    }
}
