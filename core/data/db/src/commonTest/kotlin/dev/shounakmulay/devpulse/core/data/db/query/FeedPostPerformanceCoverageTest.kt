package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostPerformanceCoverageTest {

    @Test
    fun `Verify TimelineLatest SQL`() {
        val query = FeedPostQuery(sort = FeedPostSort.PublishedNewest)
        initialPage(query).assertOrderBy("p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify FeedTimelineLatest SQL`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(FeedPostFilter.FeedIds(setOf("f1")))
        )
        initialPage(query)
            .assertSqlContains("WHERE p.feedId IN (?)")
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify BookmarkedTimelineLatest SQL`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(FeedPostFilter.Bookmarked(true))
        )
        initialPage(query)
            .assertSqlContains("WHERE p.bookmarked = ?")
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify FeedBookmarkedTimelineLatest SQL`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(
                FeedPostFilter.FeedIds(setOf("f1")),
                FeedPostFilter.Bookmarked(true)
            )
        )
        initialPage(query)
            .assertSqlContains("WHERE p.feedId IN (?) AND p.bookmarked = ?")
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify PinnedFeedsTimelineLatest SQL`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PinnedFeedsFirstLatest,
            filters = setOf(FeedPostFilter.PinnedFeed(true))
        )
        initialPage(query)
            .assertSqlContains("WHERE f.pinned = ?")
            .assertOrderBy("f.pinned DESC", "p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify UpdatedLatest SQL`() {
        val query = FeedPostQuery(sort = FeedPostSort.UpdatedNewest)
        initialPage(query).assertOrderBy("p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify CreatedLatest SQL`() {
        val query = FeedPostQuery(sort = FeedPostSort.CreatedNewest)
        initialPage(query).assertOrderBy("p.createdAt DESC", "p.id DESC")
    }

    @Test
    fun `Verify TitleAlphabetical SQL`() {
        val query = FeedPostQuery(sort = FeedPostSort.TitleAtoZ)
        initialPage(query).assertOrderBy("p.title ASC", "p.id ASC")
    }

    @Test
    fun `Verify FeedNameAlphabetical SQL`() {
        val query = FeedPostQuery(sort = FeedPostSort.FeedNameAtoZ)
        initialPage(query).assertOrderBy("f.name ASC", "p.publishedAtEpochMillis DESC", "p.id DESC")
    }
}
