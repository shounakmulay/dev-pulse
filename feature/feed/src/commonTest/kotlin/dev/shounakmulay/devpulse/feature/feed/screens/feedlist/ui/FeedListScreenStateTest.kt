package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedListSource
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedListScreenStateTest {
    @Test
    fun `Given pinned tab When searching and clearing Then pinned feeds resume`() {
        val state = FeedListScreenState(selectedTab = UISelectedTab.PINNED)
        val searching = state.copy(searchQuery = "  Kotlin  ")

        assertEquals(FeedListSource.Search("Kotlin"), searching.feedListSource)
        assertEquals(FeedListSource.Pinned, searching.copy(searchQuery = " ").feedListSource)
    }

    @Test
    fun `Given all tab When replacing and clearing query Then latest search and all feeds are selected`() {
        val searching = FeedListScreenState(searchQuery = "Kotlin")

        assertEquals(FeedListSource.Search("Compose"), searching.copy(searchQuery = "Compose").feedListSource)
        assertEquals(FeedListSource.All, searching.copy(searchQuery = "").feedListSource)
    }
}
