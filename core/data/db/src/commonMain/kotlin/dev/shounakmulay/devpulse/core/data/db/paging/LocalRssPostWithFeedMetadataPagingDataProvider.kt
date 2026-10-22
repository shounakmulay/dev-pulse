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

class LocalRssPostWithFeedMetadataPagingDataProvider(
    private val feedPostDao: FeedPostDao,
    private val query: LocalFeedPostQuery
) : LocalCursorPagingSourceDataProvider<
        FeedPostCursor,
        LocalRssPostWithFeedAndSearch
        > {

    private val queryBuilder = LocalFeedPostSqlQueryBuilder(query)

    override fun getTablesToTrack(): List<String> {
        val tables = buildList {
            addAll(
                listOf(
                    ContentFeedPostTableName,
                    "LocalRssPostUserState",
                    "LocalRssFeedUserState",
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

    override suspend fun getInitialPage(loadSize: Int): List<LocalRssPostWithFeedAndSearch> {
        return feedPostDao.getPostPage(queryBuilder.buildInitialPage(loadSize))
    }

    override suspend fun getPageAfter(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedAndSearch> {
        return feedPostDao.getPostPage(
            queryBuilder.buildPageAfter(
                cursor = cursor,
                limit = loadSize
            )
        )
    }

    override suspend fun getPageBefore(
        cursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedAndSearch> {
        return feedPostDao.getPostPage(
            queryBuilder.buildPageBefore(
                cursor = cursor,
                limit = loadSize
            )
        ).reversed()
    }

    override suspend fun getRefreshPageAround(
        anchorCursor: FeedPostCursor,
        loadSize: Int
    ): List<LocalRssPostWithFeedAndSearch> {
        val beforeLimit = loadSize / 2
        val afterLimit = loadSize - beforeLimit

        val beforeItems = getPageBefore(anchorCursor, beforeLimit)
        val afterItems = feedPostDao.getPostPage(
            queryBuilder.buildRefreshPageAround(
                cursor = anchorCursor,
                limit = afterLimit
            )
        )
        return beforeItems + afterItems
    }

    override fun getId(item: LocalRssPostWithFeedAndSearch): FeedPostCursor {
        return FeedPostCursor(
            id = item.post.id,
            sort = query.sort,
            sortValue = query.sort.extractCursorValue(item),
        )
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

    private companion object {
        const val ContentFeedPostTableName = "LocalRssContentFeedPost"
        const val FeedTableName = "LocalRssFeed"
        const val PostCategoryTableName = "LocalRssPostCategory"
        const val PostTagMappingTableName = "LocalRssPostToTagMapping"
        const val PostTagTableName = "LocalRssPostTag"
    }
}
