package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostPerformanceCoverageTest {
    @Test
    fun `Given selective positive state filters When building page Then uses positive post state predicates`() {
        initialPage(LocalFeedPostQuery(filters = setOf(
            LocalFeedPostFilter.Bookmarked(true),
            LocalFeedPostFilter.ReadStatus(true),
            LocalFeedPostFilter.PinnedFeed(true),
        )))
            .assertSqlContains("ps.bookmarked = ? AND ps.isRead = ? AND COALESCE(fs.pinned, 0) = ?")
            .assertBindings(SqlBinding.LongValue(1L), SqlBinding.LongValue(1L), SqlBinding.LongValue(1L), SqlBinding.LongValue(20L))
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.id DESC")
    }
}
