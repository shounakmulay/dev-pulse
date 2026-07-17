package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQuery

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor

interface FeedPostPagingSourceProvider {
    fun getFeedPostPagingSource(
        query: FeedPostQuery
    ): PagingSource<FeedPostCursor, LocalRssPostWithFeedMetadataProjection>
}
