package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.query.FeedPostQueryFixtures.initialPage
import kotlin.test.Test

class LocalFeedPostSortCatalogSqlTest {

    @Test
    fun `Given newest published sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest))
            .assertOrderBy("p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Given oldest published sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedOldest))
            .assertOrderBy("p.publishedAtEpochMillis ASC", "p.updatedAt ASC", "p.id ASC")
    }

    @Test
    fun `Given newest updated sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.UpdatedNewest))
            .assertOrderBy("p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Given oldest updated sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.UpdatedOldest))
            .assertOrderBy("p.updatedAt ASC", "p.id ASC")
    }

    @Test
    fun `Given newest created sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.CreatedNewest))
            .assertOrderBy("p.createdAt DESC", "p.id DESC")
    }

    @Test
    fun `Given oldest created sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.CreatedOldest))
            .assertOrderBy("p.createdAt ASC", "p.id ASC")
    }

    @Test
    fun `Given title AtoZ sort When building page Then uses correct columns`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.TitleAtoZ)
        initialPage(query).assertOrderBy("p.title ASC", "p.id ASC")
    }

    @Test
    fun `Given title ZtoA sort When building page Then uses correct columns`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.TitleZtoA)
        initialPage(query).assertOrderBy("p.title DESC", "p.id DESC")
    }

    @Test
    fun `Given feed name AtoZ sort When building page Then uses correct columns`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.FeedNameAtoZ)
        initialPage(query).assertOrderBy("f.name ASC", "p.publishedAtEpochMillis DESC", "p.id DESC")
    }

    @Test
    fun `Given feed name ZtoA sort When building page Then uses correct columns`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.FeedNameZtoA)
        initialPage(query).assertOrderBy("f.name DESC", "p.publishedAtEpochMillis DESC", "p.id DESC")
    }

    @Test
    fun `Given bookmarked first latest sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.BookmarkedFirstLatest))
            .assertOrderBy("p.bookmarked DESC", "p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }

    @Test
    fun `Given pinned feeds first latest sort When building page Then uses correct columns`() {
        initialPage(LocalFeedPostQuery(sort = LocalFeedPostSort.PinnedFeedsFirstLatest))
            .assertOrderBy("f.pinned DESC", "p.publishedAtEpochMillis DESC", "p.updatedAt DESC", "p.id DESC")
    }
}
