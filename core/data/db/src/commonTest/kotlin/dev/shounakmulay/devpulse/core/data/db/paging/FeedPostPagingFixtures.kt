package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice

object FeedPostPagingFixtures {
    fun row(id: String): LocalRssPostWithFeedAndSearch = LocalRssPostWithFeedAndSearch(
        post = LocalRssContentFeedPost(
            id = LocalUUID(id),
            feedId = LocalUUID("feed-a"),
            fingerprint = "$id-fingerprint",
            guid = "$id-guid",
            title = "Tied title",
            author = "Tied author",
            link = "https://example.com/posts/$id",
            pubDate = null,
            publishedAtEpochMillis = 1000L,
            description = "Description $id",
            content = "Content $id",
            image = null,
            audio = null,
            video = null,
            sourceName = "Feed A",
            sourceUrl = "https://example.com/feed.xml",
            categories = "",
            commentsUrl = null,
            youtubeData = null,
            rawEnclosure = null,
            rawMedia = null,
            createdAt = 100L,
            updatedAt = 500L,
        ),
        feed = LocalRssFeedIdentitySlice(
            id = LocalUUID("feed-a"),
            title = "Feed A",
            name = "Feed A",
            sourceUrl = "https://example.com/feed.xml",
            link = "https://example.com",
            createdAt = 100L,
            updatedAt = 500L,
        ),
    )
}
