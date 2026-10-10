package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedListSource
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedListScreenStateTest {
    @Test
    fun `Given pinned tab When query changes Then pinned remains the underlying source`() {
        val state = FeedListScreenState(selectedTab = UISelectedTab.PINNED)

        assertEquals(FeedListSource.Pinned, state.copy(searchQuery = "Kotlin").feedListSource)
        assertEquals(FeedListSource.Pinned, state.copy(searchQuery = "").feedListSource)
    }

    @Test
    fun `Given active query When selecting all or pinned Then source follows the selected tab`() {
        val state = FeedListScreenState(searchQuery = "Kotlin")

        assertEquals(FeedListSource.All, state.feedListSource)
        assertEquals(FeedListSource.Pinned, state.copy(selectedTab = UISelectedTab.PINNED).feedListSource)
    }
}
