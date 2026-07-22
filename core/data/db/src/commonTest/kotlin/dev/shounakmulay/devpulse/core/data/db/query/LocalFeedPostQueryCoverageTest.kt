package dev.shounakmulay.devpulse.core.data.db.query

import kotlin.test.Test
import kotlin.test.assertEquals

class LocalFeedPostQueryCoverageTest {

    @Test
    fun `Given newest published sort and no filters When classified Then returns TimelineLatest`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest)
        assertEquals(
            FeedPostQueryCoverageProfile.TimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given newest published sort and feed ids filter When classified Then returns FeedTimelineLatest`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.LocalFeedIds(setOf("f1")))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.FeedTimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given newest published sort and bookmarked filter When classified Then returns BookmarkedTimelineLatest`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.Bookmarked(true))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.BookmarkedTimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given newest published sort and feed plus bookmarked filter When classified Then returns FeedBookmarkedTimelineLatest`() {
        val query = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(
                LocalFeedPostFilter.LocalFeedIds(setOf("f1")),
                LocalFeedPostFilter.Bookmarked(true)
            )
        )
        assertEquals(
            FeedPostQueryCoverageProfile.FeedBookmarkedTimelineLatest,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given title AtoZ sort When classified Then returns TitleAlphabetical`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.TitleAtoZ)
        assertEquals(
            FeedPostQueryCoverageProfile.TitleAlphabetical,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given feed name AtoZ sort When classified Then returns FeedNameAlphabetical`() {
        val query = LocalFeedPostQuery(sort = LocalFeedPostSort.FeedNameAtoZ)
        assertEquals(
            FeedPostQueryCoverageProfile.FeedNameAlphabetical,
            FeedPostQueryCoverageProfile.classify(query)
        )
    }

    @Test
    fun `Given rare combinations When classified Then returns RareCombination`() {
        val search = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.SearchText("term"))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.RareCombination,
            FeedPostQueryCoverageProfile.classify(search)
        )

        val author = LocalFeedPostQuery(
            sort = LocalFeedPostSort.PublishedNewest,
            filters = setOf(LocalFeedPostFilter.Author(setOf("author")))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.RareCombination,
            FeedPostQueryCoverageProfile.classify(author)
        )
        
        val complex = LocalFeedPostQuery(
            sort = LocalFeedPostSort.TitleAtoZ,
            filters = setOf(LocalFeedPostFilter.Bookmarked(true))
        )
        assertEquals(
            FeedPostQueryCoverageProfile.RareCombination,
            FeedPostQueryCoverageProfile.classify(complex)
        )
    }
}
