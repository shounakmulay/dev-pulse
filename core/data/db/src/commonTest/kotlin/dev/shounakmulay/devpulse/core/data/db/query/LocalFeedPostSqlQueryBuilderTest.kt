package dev.shounakmulay.devpulse.core.data.db.query

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class FeedPostSqlQueryBuilderTest {

    @Test
    fun `Given default query When building initial page Then joins posts and feeds with keyset compatible sort`() {
        val sql = FeedPostSqlQueryBuilder(FeedPostQuery())
            .buildInitialPage(limit = 20)
            .sql

        assertContains(sql, "FROM LocalRssContentFeedPost p")
        assertContains(sql, "INNER JOIN LocalRssFeed f ON p.feedId = f.id")
        assertContains(sql, "ORDER BY p.publishedAtEpochMillis DESC")
        assertContains(sql, "p.updatedAt DESC")
        assertContains(sql, "p.id DESC")
        assertContains(sql, "LIMIT ?")
        assertFalse(sql.contains("OFFSET"))
        assertFalse(sql.contains("COALESCE"))
    }

    @Test
    fun `Given approved filters When building initial page Then applies feed tag bookmark pinned date and media clauses`() {
        val sql = FeedPostSqlQueryBuilder(
            FeedPostQuery(
                filters = setOf(
                    FeedPostFilter.FeedIds(values = setOf("feed-1", "feed-2")),
                    FeedPostFilter.TagIdsAll(values = setOf(1, 2)),
                    FeedPostFilter.Bookmarked(value = true),
                    FeedPostFilter.PinnedFeed(value = false),
                    FeedPostFilter.PublishedRange(range = FeedPostLongRange(min = 10L, max = 20L)),
                    FeedPostFilter.UpdatedRange(range = FeedPostLongRange(min = 30L, max = 40L)),
                    FeedPostFilter.HasAudio(value = true),
                    FeedPostFilter.HasVideo(value = true)
                )
            )
        ).buildInitialPage(limit = 20).sql

        assertContains(sql, "p.feedId IN (?, ?)")
        assertContains(sql, "FROM LocalRssPostToTagMapping")
        assertContains(sql, "HAVING COUNT(DISTINCT tagId) = ?")
        assertContains(sql, "p.bookmarked = ?")
        assertContains(sql, "f.pinned = ?")
        assertContains(sql, "p.publishedAtEpochMillis >= ?")
        assertContains(sql, "p.publishedAtEpochMillis <= ?")
        assertContains(sql, "p.updatedAt >= ?")
        assertContains(sql, "p.updatedAt <= ?")
        assertContains(sql, "rawEnclosure_type IS NOT NULL AND p.rawEnclosure_type LIKE 'audio/%'")
        assertContains(sql, "youtubeData_videoId IS NOT NULL")
    }

    @Test
    fun `Given negative media filters When building initial page Then negates null-safe media clauses`() {
        val sql = FeedPostSqlQueryBuilder(
            FeedPostQuery(
                filters = setOf(
                    FeedPostFilter.HasAudio(value = false),
                    FeedPostFilter.HasVideo(value = false)
                )
            )
        ).buildInitialPage(limit = 20).sql

        assertContains(sql, "NOT (")
        assertContains(sql, "rawEnclosure_type IS NOT NULL AND p.rawEnclosure_type LIKE 'audio/%'")
        assertContains(sql, "rawEnclosure_type IS NOT NULL AND p.rawEnclosure_type LIKE 'video/%'")
    }

    @Test
    fun `Given published cursor When building next page Then uses published updated and id keyset predicate`() {
        val sort = FeedPostSort.PublishedNewest
        val sql = FeedPostSqlQueryBuilder(FeedPostQuery(sort = sort))
            .buildPageAfter(
                cursor = FeedPostCursor(
                    sort = sort,
                    values = listOf(
                        FeedPostCursorValue.LongValue(100L),
                        FeedPostCursorValue.LongValue(90L),
                        FeedPostCursorValue.TextValue("post-1")
                    )
                ),
                limit = 20
            )
            .sql

        assertContains(sql, "p.publishedAtEpochMillis <")
        assertContains(sql, "p.updatedAt <")
        assertContains(sql, "p.id <")
        assertFalse(sql.contains("OFFSET"))
        assertFalse(sql.contains("COALESCE"))
    }

    @Test
    fun `Given title ascending cursor When building next page Then uses text keyset predicate`() {
        val sort = FeedPostSort.TitleAtoZ
        val sql = FeedPostSqlQueryBuilder(
            FeedPostQuery(sort = sort)
        ).buildPageAfter(
            cursor = FeedPostCursor(
                sort = sort,
                values = listOf(
                    FeedPostCursorValue.TextValue("A title"),
                    FeedPostCursorValue.TextValue("post-1")
                )
            ),
            limit = 20
        ).sql

        assertContains(sql, "ORDER BY p.title ASC, p.id ASC")
        assertContains(sql, "p.title >")
        assertContains(sql, "p.id >")
        assertFalse(sql.contains("COALESCE"))
    }
}
