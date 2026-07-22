package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostSearchSqlTest {

    @Test
    fun `Given basic text search When building page Then applies lower and wildcard matching`() {
        val query = LocalFeedPostQuery(filters = setOf(LocalFeedPostFilter.SearchText("Hello")))
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
        val query = LocalFeedPostQuery(filters = setOf(LocalFeedPostFilter.SearchText("100% discount_")))
        initialPage(query)
            .assertBindings(
                FeedPostQueryFixtures.text("%100\\% discount\\_%"),
                FeedPostQueryFixtures.text("%100\\% discount\\_%"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given search with slashes When building page Then escapes them correctly`() {
        val query = LocalFeedPostQuery(filters = setOf(LocalFeedPostFilter.SearchText("path\\name")))
        initialPage(query)
            .assertBindings(
                FeedPostQueryFixtures.text("%path\\\\name%"),
                FeedPostQueryFixtures.text("%path\\\\name%"),
                FeedPostQueryFixtures.long(20L)
            )
    }

    @Test
    fun `Given empty or blank search When building page Then ignores filter completely`() {
        val emptyQuery = LocalFeedPostQuery(filters = setOf(LocalFeedPostFilter.SearchText("")))
        initialPage(emptyQuery).assertSqlExcludes("LIKE")

        val blankQuery = LocalFeedPostQuery(filters = setOf(LocalFeedPostFilter.SearchText("   ")))
        initialPage(blankQuery).assertSqlExcludes("LIKE")
    }
}
