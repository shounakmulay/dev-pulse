package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery

interface FeedPostPagingSourceProvider {
    fun getFeedPostPagingSource(
        query: LocalFeedPostQuery
    ): PagingSource<FeedPostCursor, LocalRssPostWithFeedAndSearch>
}
