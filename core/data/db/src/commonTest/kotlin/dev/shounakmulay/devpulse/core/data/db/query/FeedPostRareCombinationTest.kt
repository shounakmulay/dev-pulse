package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostRareCombinationTest {
    @Test
    fun `Given search and state filters When building page Then intersects FTS matches before limiting`() {
        initialPage(LocalFeedPostQuery(filters = setOf(
            LocalFeedPostFilter.SearchText("rare"),
            LocalFeedPostFilter.Bookmarked(true),
            LocalFeedPostFilter.ReadStatus(false),
        )))
            .assertSqlContains("LocalRssContentFeedPostFts MATCH ?")
            .assertSqlContains("LocalRssPostContentFts MATCH ?")
            .assertSqlContains("ps.bookmarked = ? AND COALESCE(ps.isRead, 0) = ?")
            .assertSqlContains("(ph.postRowId IS NOT NULL OR ah.postId IS NOT NULL)")
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.id DESC")
    }

    @Test
    fun `Given author and source filters When building page Then normalizes and binds external values`() {
        initialPage(LocalFeedPostQuery(filters = setOf(
            LocalFeedPostFilter.Author(setOf("Writer")),
            LocalFeedPostFilter.SourceFeed(setOf("URL")),
        )))
            .assertSqlContains("LOWER(p.author) IN (?)")
            .assertSqlContains("LOWER(p.sourceUrl) IN (?)")
            .assertBindings(SqlBinding.Text("writer"), SqlBinding.Text("url"), SqlBinding.LongValue(20L))
    }

    @Test
    fun `Given category media and tag filters When building page Then intersects them under title ordering`() {
        initialPage(LocalFeedPostQuery(
            sort = LocalFeedPostSort.TitleAtoZ,
            filters = setOf(
                LocalFeedPostFilter.Category(setOf("tech")),
                LocalFeedPostFilter.HasImage(true),
                LocalFeedPostFilter.TagIdsAny(setOf(1, 2)),
            ),
        ))
            .assertSqlContains("p.id IN (SELECT postId FROM LocalRssPostCategory WHERE LOWER(category) IN (?))")
            .assertSqlContains("AND (p.image IS NOT NULL AND p.image != '')")
            .assertSqlContains("AND p.id IN (SELECT postId FROM LocalRssPostToTagMapping WHERE tagId IN (?, ?))")
            .assertBindings(SqlBinding.Text("tech"), SqlBinding.LongValue(1L), SqlBinding.LongValue(2L), SqlBinding.LongValue(20L))
            .assertOrderBy("p.title ASC", "p.id DESC")
    }

}
