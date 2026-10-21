package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class LocalFeedPostQueryCoverageTest {
    @Test
    fun `Given negative state filters When building page Then absent rows use unread unbookmarked unpinned defaults`() {
        initialPage(LocalFeedPostQuery(filters = setOf(
            LocalFeedPostFilter.ReadStatus(false),
            LocalFeedPostFilter.Bookmarked(false),
            LocalFeedPostFilter.PinnedFeed(false),
        )))
            .assertSqlContains("COALESCE(ps.isRead, 0) = ? AND COALESCE(ps.bookmarked, 0) = ? AND COALESCE(fs.pinned, 0) = ?")
            .assertBindings(SqlBinding.LongValue(0L), SqlBinding.LongValue(0L), SqlBinding.LongValue(0L), SqlBinding.LongValue(20L))
    }

    @Test
    fun `Given unset state filters When building page Then leaves the result scope unrestricted`() {
        initialPage(LocalFeedPostQuery(filters = setOf(
            LocalFeedPostFilter.ReadStatus(null),
            LocalFeedPostFilter.Bookmarked(null),
            LocalFeedPostFilter.PinnedFeed(null),
        )))
            .assertSqlExcludes("WHERE")
            .assertBindings(SqlBinding.LongValue(20L))
    }
}
