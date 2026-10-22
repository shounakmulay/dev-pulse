package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class FeedPostTimelineSqlTest {
    @Test
    fun `Given timeline sorts When building pages Then uses stored publication and id without offset`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest))
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.id DESC")
            .assertSqlExcludes("ORDER BY COALESCE")
            .assertSqlExcludes("OFFSET")
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedOldest))
            .assertOrderBy("p.publishedAtEpochMillis ASC", "p.id ASC")
            .assertSqlExcludes("ORDER BY COALESCE")
            .assertSqlExcludes("OFFSET")
    }
}
