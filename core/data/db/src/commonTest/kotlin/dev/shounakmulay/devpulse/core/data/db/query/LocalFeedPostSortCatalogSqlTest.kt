package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class LocalFeedPostSortCatalogSqlTest {
    @Test
    fun `Given supported sorts When building pages Then orders by the stored column and stable id`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest))
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.id DESC")
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedOldest))
            .assertOrderBy("p.publishedAtEpochMillis ASC", "p.id ASC")
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.TitleAtoZ))
            .assertOrderBy("p.title ASC", "p.id DESC")
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.TitleZtoA))
            .assertOrderBy("p.title DESC", "p.id DESC")
    }
}
