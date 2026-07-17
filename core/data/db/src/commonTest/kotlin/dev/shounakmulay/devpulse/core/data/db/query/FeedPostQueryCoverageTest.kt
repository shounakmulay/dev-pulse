package dev.shounakmulay.devpulse.core.data.db.query

import kotlin.test.Test
import kotlin.test.assertEquals

class FeedPostQueryCoverageTest {

    @Test
    fun `Given newest published sort and no filters When classified Then returns TimelineLatest`() {
        val query = FeedPostQuery(sort = FeedPostSort.PublishedNewest)
        assertEquals(
            FeedPostQueryCoverageProfile.TimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given newest published sort and feed ids filter When classified Then returns FeedTimelineLatest`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(FeedPostFilter.FeedIds(setOf("f1")))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.FeedTimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given newest published sort and bookmarked filter When classified Then returns BookmarkedTimelineLatest`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(FeedPostFilter.Bookmarked(true))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.BookmarkedTimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given newest published sort and feed plus bookmarked filter When classified Then returns FeedBookmarkedTimelineLatest`() {
        val query = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(
                FeedPostFilter.FeedIds(setOf("f1")),
                FeedPostFilter.Bookmarked(true)
            )
        )
        assertEquals(
            FeedPostQueryCoverageProfile.FeedBookmarkedTimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given title AtoZ sort When classified Then returns TitleAlphabetical`() {
        val query = FeedPostQuery(sort = FeedPostSort.TitleAtoZ)
        assertEquals(
            FeedPostQueryCoverageProfile.TitleAlphabetical,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given feed name AtoZ sort When classified Then returns FeedNameAlphabetical`() {
        val query = FeedPostQuery(sort = FeedPostSort.FeedNameAtoZ)
        assertEquals(
            FeedPostQueryCoverageProfile.FeedNameAlphabetical,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given rare combinations When classified Then returns RareCombination`() {
        val search = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(FeedPostFilter.SearchText("term"))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.RareCombination,
            FeedPostQueryCoverageProfile.classify(search)
        )

        val author = FeedPostQuery(
            sort = FeedPostSort.PublishedNewest,
            filters = setOf(FeedPostFilter.Author(setOf("author")))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.RareCombination,
            FeedPostQueryCoverageProfile.classify(author)
        )
        
        val complex = FeedPostQuery(
            sort = FeedPostSort.TitleAtoZ,
            filters = setOf(FeedPostFilter.Bookmarked(true))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.RareCombination,
            FeedPostQueryCoverageProfile.classify(complex)
        )
    }
}
