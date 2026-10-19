package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.FtsQuerySanitizer
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostFilter
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSqlQueryBuilder
import dev.shounakmulay.devpulse.core.data.db.query.SqlBinding
import dev.shounakmulay.devpulse.core.logging.DPLog
import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlin.time.TimeSource

class LocalRssPostWithFeedMetadataPagingDataProvider(
    private val feedPostDao: FeedPostDao,
    private val query: LocalFeedPostQuery,
    private val tracer: Tracer = NoOpTelemetry,
    private val metrics: Metrics = NoOpTelemetry,
) : LocalCursorPagingSourceDataProvider<
        FeedPostCursor,
        LocalRssPostWithFeedAndSearch
        > {

    private val queryBuilder = LocalFeedPostSqlQueryBuilder(query)
    private val logger = DPLog.tag(Tag)

    override fun getTablesToTrack(): List<String> {
        val tables = buildList {
            addAll(
                listOf(
                    ContentFeedPostTableName,
                    FeedTableName,
                    PostCategoryTableName,
                    PostTagMappingTableName,
                    PostTagTableName
                )
            )
            val hasSearch = query.filters.any {
                it is LocalFeedPostFilter.SearchText && FtsQuerySanitizer().sanitize(it.value).isNotBlank()
            }
            if (hasSearch) {
                add("LocalRssContentFeedPostFts")
                add("LocalRssPostContentFts")
            }
        }
        return tables
    }

    override suspend fun getInitialPage(loadSize: Int): List<LocalRssPostWithFeedAndSearch> = measured("initial") {
        logger.d {
            "[FEED-PAGING] initial:start loadSize=$loadSize sort=${query.sort} " +
                    "filterCount=${query.filters.size}"
        }
        val items = feedPostDao.getPostPage(queryBuilder.buildInitialPage(loadSize))
        logger.d { "[FEED-PAGING] initial:end ${items.summary()}" }
        items
    }

    override suspend fun getPageAfter(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedAndSearch> = measured("append") {
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
        items
    }

    override suspend fun getPageBefore(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedAndSearch> = measured("prepend") {
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
        items
    }

    override suspend fun getRefreshPageAround(
        anchorCursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedAndSearch> = measured("refresh") {
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
        items
    }

    override fun getId(item: LocalRssPostWithFeedAndSearch): FeedPostCursor {
        val cursor = FeedPostCursor(
            id = item.post.id,
            sort = query.sort,
            sortValue = query.sort.extractCursorValue(item),
        )
        logger.v { "[FEED-PAGING] cursorFromItem sort=${cursor.sort}" }
        return cursor
    }

    private fun LocalFeedPostSort.extractCursorValue(
        item: LocalRssPostWithFeedAndSearch
    ): SqlBinding {
        return when (this) {
            LocalFeedPostSort.PublishedNewest,
            LocalFeedPostSort.PublishedOldest -> SqlBinding.LongValue(item.post.publishedAtEpochMillis)

            LocalFeedPostSort.TitleAtoZ,
            LocalFeedPostSort.TitleZtoA -> SqlBinding.Text(item.post.title)
        }
    }

    private fun FeedPostCursor.summary(): String {
        return "{sort=$sort}"
    }

    private fun List<LocalRssPostWithFeedAndSearch>.summary(): String {
        return "count=$size"
    }

    private suspend fun measured(
        kind: String,
        block: suspend () -> List<LocalRssPostWithFeedAndSearch>,
    ): List<LocalRssPostWithFeedAndSearch> {
        val started = TimeSource.Monotonic.markNow()
        val attributes = MetricAttributes.of("kind" to kind)
        try {
            return tracer.trace(LoadOperation, SafeAttributes.of("kind" to AttributeValue.Text(kind))) {
                block()
            }.also { metrics.record(RowCount, it.size.toDouble(), attributes) }
        } finally {
            metrics.record(LoadDuration, started.elapsedNow().inWholeNanoseconds / 1_000_000.0, attributes)
        }
    }

    private companion object {
        val Kinds = setOf("initial", "append", "prepend", "refresh")
        val LoadOperation = OperationDefinition("db.feed_posts_load", AttributeSchema(mapOf("kind" to AttributeRule.Choice(Kinds))))
        val Dimensions = MetricDimensions(mapOf("kind" to Kinds))
        val LoadDuration = HistogramDefinition("db.feed_posts_duration", MetricUnit.Milliseconds, Dimensions)
        val RowCount = HistogramDefinition("db.feed_posts_rows", MetricUnit.Count, Dimensions)
        const val Tag = "FeedPostPagingProvider"
        const val ContentFeedPostTableName = "LocalRssContentFeedPost"
        const val FeedTableName = "LocalRssFeed"
        const val PostCategoryTableName = "LocalRssPostCategory"
        const val PostTagMappingTableName = "LocalRssPostToTagMapping"
        const val PostTagTableName = "LocalRssPostTag"
    }
}
