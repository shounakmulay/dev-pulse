package dev.shounakmulay.devpulse.core.data.db.query

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class FeedPostTimelineSqlTest {

    @Test
    fun `Given published newest sort When building initial page Then orders by published updated and id descending`() {
        val sql = timelineSql(FeedPostSort.PublishedNewest)

        assertContains(
            sql.normalized(),
            "ORDER BY p.publishedAtEpochMillis DESC, p.updatedAt DESC, p.id DESC LIMIT ?"
        )
        assertTimelineSqlDoesNotUseFallbackPaging(sql)
    }

    @Test
    fun `Given published oldest sort When building initial page Then orders by published updated and id ascending`() {
        val sql = timelineSql(FeedPostSort.PublishedOldest)

        assertContains(
            sql.normalized(),
            "ORDER BY p.publishedAtEpochMillis ASC, p.updatedAt ASC, p.id ASC LIMIT ?"
        )
        assertTimelineSqlDoesNotUseFallbackPaging(sql)
    }

    private fun timelineSql(sort: FeedPostSort): String {
        return FeedPostSqlQueryBuilder(
            FeedPostQuery(sort = sort)
        ).buildInitialPage(limit = FeedPostQueryFixtures.DefaultLimit).sql
    }

    private fun assertTimelineSqlDoesNotUseFallbackPaging(sql: String) {
        assertFalse(
            actual = sql.contains("COALESCE"),
            message = "Timeline SQL must sort by non-null stored columns instead of COALESCE fallbacks: $sql"
        )
        assertFalse(
            actual = sql.contains("OFFSET"),
            message = "Timeline SQL must use keyset paging instead of OFFSET: $sql"
        )
        assertFalse(
            actual = sql.contains("publishedSortAt"),
            message = "Timeline SQL must use p.publishedAtEpochMillis directly: $sql"
        )
    }

    private fun String.normalized(): String {
        return trim()
            .replace(Regex("\\s+"), " ")
    }
}
