package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import kotlin.test.Test

class FeedPostCursorPredicateTest {
    @Test
    fun `Given published cursor When appending Then binds the sort value twice and the stable id`() {
        val cursor = FeedPostCursor(LocalUUID("post-100"), LocalFeedPostSort.PublishedNewest, SqlBinding.LongValue(1000L))
        LocalFeedPostSqlQueryBuilder(LocalFeedPostQuery(sort = cursor.sort))
            .buildPageAfter(cursor = cursor, limit = 20)
            .assertSqlContains("p.publishedAtEpochMillis < ? OR (p.publishedAtEpochMillis = ? AND p.id < ?)")
            .assertSqlExcludes("OFFSET")
            .assertBindings(SqlBinding.LongValue(1000L), SqlBinding.LongValue(1000L), SqlBinding.Text("post-100"), SqlBinding.LongValue(20L))
    }

    @Test
    fun `Given title cursor When appending and prepending Then reverses both comparison directions`() {
        val cursor = FeedPostCursor(LocalUUID("post-100"), LocalFeedPostSort.TitleAtoZ, SqlBinding.Text("A title"))
        val builder = LocalFeedPostSqlQueryBuilder(LocalFeedPostQuery(sort = cursor.sort))
        builder.buildPageAfter(cursor = cursor, limit = 20)
            .assertSqlContains("p.title > ? OR (p.title = ? AND p.id < ?)")
            .assertOrderBy("p.title ASC", "p.id DESC")
        builder.buildPageBefore(cursor = cursor, limit = 20)
            .assertSqlContains("p.title < ? OR (p.title = ? AND p.id > ?)")
            .assertOrderBy("p.title DESC", "p.id ASC")
        builder.buildRefreshPageAround(cursor = cursor, limit = 20)
            .assertSqlContains("p.title > ? OR (p.title = ? AND p.id <= ?)")
    }
}
