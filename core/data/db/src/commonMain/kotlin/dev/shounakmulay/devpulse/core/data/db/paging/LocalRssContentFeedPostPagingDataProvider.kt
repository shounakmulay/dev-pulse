package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.dao.FeedContentDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost

class LocalRssContentFeedPostPagingDataProvider(
    private val feedContentDao: FeedContentDao
) : LocalCursorPagingSourceDataProvider<LocalRssContentFeedPostCursor, LocalRssContentFeedPost> {

    override fun getTablesToTrack(): List<String> {
        return listOf(TABLE_NAME)
    }

    override suspend fun getInitialPage(loadSize: Int): List<LocalRssContentFeedPost> {
        return feedContentDao.getInitialPage(loadSize)
    }

    override suspend fun getPageAfter(
        cursor: LocalRssContentFeedPostCursor,
        loadSize: Int
    ): List<LocalRssContentFeedPost> {
        return feedContentDao.getPageAfter(
            id = cursor.id,
            limit = loadSize
        )
    }

    override suspend fun getRefreshPageAround(
        anchorCursor: LocalRssContentFeedPostCursor,
        loadSize: Int
    ): List<LocalRssContentFeedPost> {
        return feedContentDao.getRefreshPageAround(
            id = anchorCursor.id,
            limit = loadSize
        )
    }

    override fun getId(item: LocalRssContentFeedPost): LocalRssContentFeedPostCursor {
        return LocalRssContentFeedPostCursor(
            id = item.id
        )
    }

    private companion object {
        const val TABLE_NAME = "LocalRssContentFeedPost"
    }
}
