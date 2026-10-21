package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostRareCombinationTest {

    @Test
    fun `Verify SearchText correctness`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.SearchText("rare"))
        )
        initialPage(query)
            .assertSqlContains("(LOWER(p.title) LIKE ? ESCAPE '\\' OR LOWER(p.description) LIKE ? ESCAPE '\\')")
    }

    @Test
    fun `Verify Author correctness`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.Author(setOf("writer")))
        )
        initialPage(query)
            .assertSqlContains("LOWER(p.author) IN (?)")
    }

    @Test
    fun `Verify Source correctness`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.SourceLocalFeed(setOf("url")))
        )
        initialPage(query)
            .assertSqlContains("LOWER(p.sourceUrl) IN (?)")
    }

    @Test
    fun `Verify multi filter combination correctness`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.UpdatedNewest,
            filters = setOf(
                LocalFeedPostFilter.Category(setOf("tech")),
                LocalFeedPostFilter.HasImage(true),
                LocalFeedPostFilter.TagIdsAll(setOf(1, 2))
            )
        )
        initialPage(query)
            .assertSqlContains("p.id IN (SELECT postId FROM LocalRssPostCategory WHERE LOWER(category) IN (?))")
            .assertSqlContains("(p.image IS NOT NULL AND p.image != '')")
            .assertSqlContains("GROUP BY postId HAVING COUNT(DISTINCT tagId) = ?")
            .assertOrderBy("p.updatedAt DESC", "p.id DESC")
    }
}
