package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostSearchSqlTest {

    @Test
    fun `Given basic text search When building page Then applies lower and wildcard matching`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.SearchText("Hello")))
        initialPage(query)
            .assertSqlContains("(LOWER(p.title) LIKE ? ESCAPE '\\' OR LOWER(p.description) LIKE ? ESCAPE '\\')")
            .assertBindings(
                FeedPostQueryFixtures.text("%hello%"),
                FeedPostQueryFixtures.text("%hello%"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given search with LIKE wildcards When building page Then escapes them`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.SearchText("100% discount_")))
        initialPage(query)
            .assertBindings(
                FeedPostQueryFixtures.text("%100\\% discount\\_%"),
                FeedPostQueryFixtures.text("%100\\% discount\\_%"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given search with slashes When building page Then escapes them correctly`() {
        val query = FeedPostQuery(filters = setOf(FeedPostFilter.SearchText("path\\name")))
        initialPage(query)
            .assertBindings(
                FeedPostQueryFixtures.text("%path\\\\name%"),
                FeedPostQueryFixtures.text("%path\\\\name%"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given empty or blank search When building page Then ignores filter completely`() {
        val emptyQuery = FeedPostQuery(filters = setOf(FeedPostFilter.SearchText("")))
        initialPage(emptyQuery).assertSqlExcludes("LIKE")

        val blankQuery = FeedPostQuery(filters = setOf(FeedPostFilter.SearchText("   ")))
        initialPage(blankQuery).assertSqlExcludes("LIKE")
    }
}
