package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigatorFeedDeletionTest {
    @Test
    fun `Given repeated feed details When deleting the feed Then every owned route is removed`() {
        val state = NavigationState(
            rootStack = NavBackStack(
                Screen.Tabs,
                Screen.Tabs.Feed.FeedDetail(UUID("feed-a")),
                Screen.Tabs.Feed.PostDetail(UUID("article-a")),
                Screen.Tabs.Feed.FeedDetail(UUID("feed-a")),
            ),
            tabsStart = Screen.Tabs.Home,
            selectedTab = mutableStateOf(Screen.Tabs.Feed),
            tabsBackStacks = mapOf(
                Screen.Tabs.Home to NavBackStack(Screen.Tabs.Home),
                Screen.Tabs.Feed to NavBackStack(Screen.Tabs.Feed),
            ),
        )
        val navigator = Navigator(state, DPLog.tag("NavigatorFeedDeletionTest"))

        navigator.navigateBack()

        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
    }
}
