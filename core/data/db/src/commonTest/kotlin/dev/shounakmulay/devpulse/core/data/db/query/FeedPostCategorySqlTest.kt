package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostCategorySqlTest {

    @Test
    fun `Given exact category match When building page Then uses category mapping table directly`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.Category(setOf("Technology", "NEWS"))))
        initialPage(query)
            .assertSqlContains("WHERE p.id IN (SELECT postId FROM LocalRssPostCategory WHERE LOWER(category) IN (?, ?))")
            .assertBindings(
                FeedPostQueryFixtures.text("news"),
                FeedPostQueryFixtures.text("technology"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given empty category filter When building page Then ignores filter completely`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.Category(emptySet())))
        initialPage(query).assertSqlExcludes("LocalRssPostCategory")
    }
}
