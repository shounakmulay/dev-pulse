package dev.shounakmulay.devpulse.core.data.db.query

import kotlin.test.Test
import kotlin.test.assertFalse

class FeedPostCursorPredicateTest {

    @Test
    fun `Given long descending cursor When building next page Then predicate advances by long term and id`() {
        val sql = cursorPageSql(
            sort = FeedPostSort.PublishedNewest,
            values = listOf(
                FeedPostCursorValue.LongValue(1_000L),
                FeedPostCursorValue.LongValue(900L),
                FeedPostCursorValue.TextValue("post-100")
            )
        )

        assertContainsInOrder(
            sql = sql,
            "p.publishedAtEpochMillis < ?",
            "p.publishedAtEpochMillis = ?",
            "p.id < ?"
        )
        assertNoOffsetOrFallbackExpressions(sql)
    }

    @Test
    fun `Given text ascending cursor When building next page Then predicate advances by text term and id`() {
        val sql = cursorPageSql(
            sort = FeedPostSort.TitleAtoZ,
            values = listOf(
                FeedPostCursorValue.TextValue("A title"),
                FeedPostCursorValue.TextValue("post-100")
            )
        )

        assertContainsInOrder(
            sql = sql,
            "p.title > ?",
            "p.title = ?",
            "p.id > ?"
        )
        assertNoOffsetOrFallbackExpressions(sql)
    }

    @Test
    fun `Given boolean first cursor When building next page Then predicate advances by boolean term before timeline terms`() {
        val sql = cursorPageSql(
            sort = FeedPostSort.BookmarkedFirstLatest,
            values = listOf(
                FeedPostCursorValue.BooleanValue(true),
                FeedPostCursorValue.LongValue(1_000L),
                FeedPostCursorValue.LongValue(900L),
                FeedPostCursorValue.TextValue("post-100")
            )
        )

        assertContainsInOrder(
            sql = sql,
            "p.bookmarked < ?",
            "p.bookmarked = ?",
            "p.publishedAtEpochMillis < ?",
            "p.id < ?"
        )
        assertNoOffsetOrFallbackExpressions(sql)
    }

    @Test
    fun `Given feed derived cursor When building next page Then predicate advances by feed term before post terms`() {
        val sql = cursorPageSql(
            sort = FeedPostSort.FeedNameAtoZ,
            values = listOf(
                FeedPostCursorValue.TextValue("Android Weekly"),
                FeedPostCursorValue.LongValue(1_000L),
                FeedPostCursorValue.TextValue("post-100")
            )
        )

        assertContainsInOrder(
            sql = sql,
            "f.name > ?",
            "f.name = ?",
            "p.publishedAtEpochMillis < ?",
            "p.id < ?"
        )
        assertNoOffsetOrFallbackExpressions(sql)
    }

    @Test
    fun `Given pinned feed cursor When building next page Then predicate can use feed boolean term`() {
        val sql = cursorPageSql(
            sort = FeedPostSort.PinnedFeedsFirstLatest,
            values = listOf(
                FeedPostCursorValue.BooleanValue(true),
                FeedPostCursorValue.LongValue(1_000L),
                FeedPostCursorValue.LongValue(900L),
                FeedPostCursorValue.TextValue("post-100")
            )
        )

        assertContainsInOrder(
            sql = sql,
            "f.pinned < ?",
            "f.pinned = ?",
            "p.publishedAtEpochMillis < ?",
            "p.id < ?"
        )
        assertNoOffsetOrFallbackExpressions(sql)
    }

    private fun cursorPageSql(
        sort: FeedPostSort,
        values: List<FeedPostCursorValue>
    ): String {
        return FeedPostSqlQueryBuilder(
            FeedPostQuery(
                sort = sort,
                cursor = FeedPostCursor(
                    sort = sort,
                    values = values
                )
            )
        ).buildInitialPage(limit = 20).sql.normalizedSql()
    }

    private fun assertContainsInOrder(
        sql: String,
        vararg fragments: String
    ) {
        var nextStartIndex = 0
        fragments.forEach { fragment ->
            val normalizedFragment = fragment.normalizedSql()
            val fragmentIndex = sql.indexOf(
                string = normalizedFragment,
                startIndex = nextStartIndex
            )
            kotlin.test.assertTrue(
                actual = fragmentIndex >= 0,
                message = "Expected SQL to contain <$fragment> after index $nextStartIndex but was <$sql>"
            )
            nextStartIndex = fragmentIndex + normalizedFragment.length
        }
    }

    private fun assertNoOffsetOrFallbackExpressions(sql: String) {
        assertFalse(
            actual = sql.contains("OFFSET"),
            message = "Expected cursor pagination without OFFSET but was <$sql>"
        )
        assertFalse(
            actual = sql.contains("COALESCE"),
            message = "Expected stored non-null sort columns without COALESCE but was <$sql>"
        )
        assertFalse(
            actual = sql.contains("IFNULL"),
            message = "Expected stored non-null sort columns without IFNULL but was <$sql>"
        )
    }

    private fun String.normalizedSql(): String {
        return trim()
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(separator = " ")
    }
}
