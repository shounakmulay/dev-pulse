package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostFilterCatalogSqlTest {

    @Test
    fun `Given feed ids filter When building page Then uses IN clause with correct placeholders`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.FeedIds(setOf("f1", "f2"))))
        initialPage(query)
            .assertSqlContains("WHERE p.feedId IN (?, ?)")
            .assertBindings(
                FeedPostQueryFixtures.text("f1"),
                FeedPostQueryFixtures.text("f2"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given empty feed ids filter When building page Then ignores filter completely`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.FeedIds(emptySet())))
        initialPage(query).assertSqlExcludes("WHERE")
    }

    @Test
    fun `Given tag ids any filter When building page Then uses IN clause with SELECT from mapping table`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.TagIdsAny(setOf(1, 2))))
        initialPage(query)
            .assertSqlContains("WHERE p.id IN (SELECT postId FROM LocalRssPostToTagMapping WHERE tagId IN (?, ?))")
            .assertBindings(
                FeedPostQueryFixtures.long(1L),
                FeedPostQueryFixtures.long(2L),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given tag ids all filter When building page Then uses IN clause with GROUP BY and HAVING COUNT`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.TagIdsAll(setOf(1, 2))))
        initialPage(query)
            .assertSqlContains(
                "WHERE p.id IN ( SELECT postId FROM LocalRssPostToTagMapping WHERE tagId IN (?, ?) GROUP BY postId HAVING COUNT(DISTINCT tagId) = ? )"
            )
            .assertBindings(
                FeedPostQueryFixtures.long(1L),
                FeedPostQueryFixtures.long(2L),
                FeedPostQueryFixtures.long(2L),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given boolean filters When building page Then uses exact matching`() {
        val query = FeedPostQuery(
            filters = setOf(
                FeedPostFilter.Bookmarked(true),
                FeedPostFilter.PinnedFeed(false)
            )
        )
        initialPage(query)
            .assertSqlContains("WHERE p.bookmarked = ? AND f.pinned = ?")
            .assertBindings(
                FeedPostQueryFixtures.boolean(true),
                FeedPostQueryFixtures.boolean(false),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given range filters When building page Then applies bounds correctly`() {
        val query = FeedPostQuery(
            filters = setOf(
                FeedPostFilter.PublishedRange(FeedPostLongRange(min = 10L, max = 20L)),
                FeedPostFilter.UpdatedRange(FeedPostLongRange(min = 30L))
            )
        )
        initialPage(query)
            .assertSqlContains("WHERE p.publishedAtEpochMillis >= ? AND p.publishedAtEpochMillis <= ? AND p.updatedAt >= ?")
            .assertBindings(
                FeedPostQueryFixtures.long(10L),
                FeedPostQueryFixtures.long(20L),
                FeedPostQueryFixtures.long(30L),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given has image filter When building page Then checks image null and empty`() {
        initialPage(FeedPostQuery(filters = setOf(FeedPostFilter.HasImage(true))))
            .assertSqlContains("WHERE (p.image IS NOT NULL AND p.image != '')")
            
        initialPage(FeedPostQuery(filters = setOf(FeedPostFilter.HasImage(false))))
            .assertSqlContains("WHERE NOT ((p.image IS NOT NULL AND p.image != ''))")
    }
}
