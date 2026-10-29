package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigatorFeedDeletionTest {
    @Test
    fun `Given repeated feed details When deleting the feed Then every owned route is removed`() {
        val state = state(
            Screen.Tabs,
            Screen.Tabs.Feed.FeedDetail(FeedA),
            Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA),
            Screen.Tabs.Feed.FeedDetail(FeedA),
        )
        val navigator = navigator(state)

        assertTrue(navigator.removeFeedRoutes(FeedA))

        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
        assertFalse(navigator.removeFeedRoutes(FeedA))
        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
        assertEquals(Screen.Tabs.Feed, state.selectedTab)
    }

    @Test
    fun `Given interleaved history and hidden tabs When deleting Then unrelated routes keep their order`() {
        val state = state(
            Screen.Tabs,
            Screen.Tabs.Feed.FeedDetail(FeedA),
            Screen.Settings,
            Screen.Tabs.Feed.PostDetail(id = ArticleB, feedId = FeedB),
            Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA),
            Screen.Tabs.Feed.FeedDetail(FeedB),
            Screen.Tabs.Feed.FeedDetail(FeedA),
            Screen.WebView("https://example.com"),
        )
        state.tabsBackStacks.getValue(Screen.Tabs.Home).addAll(
            listOf(
                Screen.Tabs.Feed.FeedDetail(FeedA),
                Screen.Tabs.Feed.PostDetail(id = ArticleB, feedId = FeedB),
                Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA),
            ),
        )
        state.tabsBackStacks.getValue(Screen.Tabs.Feed).addAll(
            listOf(Screen.Tabs.Feed.FeedList, Screen.Tabs.Feed.FeedDetail(FeedA)),
        )
        val navigator = navigator(state)

        navigator.removeFeedRoutes(FeedA)

        assertEquals(
            listOf(
                Screen.Tabs,
                Screen.Settings,
                Screen.Tabs.Feed.PostDetail(id = ArticleB, feedId = FeedB),
                Screen.Tabs.Feed.FeedDetail(FeedB),
                Screen.WebView("https://example.com"),
            ),
            state.rootStack.toList(),
        )
        assertEquals(
            listOf(Screen.Tabs.Home, Screen.Tabs.Feed.PostDetail(id = ArticleB, feedId = FeedB)),
            state.tabsBackStacks.getValue(Screen.Tabs.Home).toList(),
        )
        assertEquals(
            listOf(Screen.Tabs.Feed, Screen.Tabs.Feed.FeedList),
            state.tabsBackStacks.getValue(Screen.Tabs.Feed).toList(),
        )
        assertEquals(Screen.Tabs.Feed, state.selectedTab)
        navigator.navigateBack()
        assertEquals(Screen.Tabs.Feed.FeedDetail(FeedB), state.rootStack.last())
    }

    @Test
    fun `Given only a feed owned deep link When deleting Then navigation falls back to Home`() {
        val state = state(Screen.Tabs.Feed.FeedDetail(FeedA))

        navigator(state).removeFeedRoutes(FeedA)

        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
        assertEquals(Screen.Tabs.Home, state.selectedTab)
        assertEquals(listOf<Screen>(Screen.Tabs.Home), state.tabsBackStacks.getValue(Screen.Tabs.Home).toList())
    }

    @Test
    fun `Given a restored article route When deleting its feed Then its serialized ownership is retained`() {
        val route: Screen = Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA)
        val restored = Json.decodeFromString<Screen>(Json.encodeToString(route))
        val state = state(Screen.Tabs, restored, Screen.Tabs.Feed.FeedDetail(FeedA))

        navigator(state).removeFeedRoutes(FeedA)

        assertEquals(Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA), restored)
        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
    }

    @Test
    fun `Given an older article route When restoring Then absent ownership still decodes`() {
        val restored = Json.decodeFromString<Screen.Tabs.Feed.PostDetail>("""{"id":"article-a"}""")
        val state = state(Screen.Tabs, restored)

        assertFalse(navigator(state).removeFeedRoutes(FeedA))

        assertEquals(Screen.Tabs.Feed.PostDetail(ArticleA), restored)
        assertEquals(listOf(Screen.Tabs, Screen.Tabs.Feed.PostDetail(ArticleA)), state.rootStack.toList())
    }

    private fun state(vararg root: Screen) = NavigationState(
        rootStack = NavBackStack(*root),
        tabsStart = Screen.Tabs.Home,
        selectedTab = mutableStateOf(Screen.Tabs.Feed),
        tabsBackStacks = mapOf(
            Screen.Tabs.Home to NavBackStack(Screen.Tabs.Home),
            Screen.Tabs.Feed to NavBackStack(Screen.Tabs.Feed),
        ),
    )

    private fun navigator(state: NavigationState) = Navigator(state, DPLog.tag("NavigatorFeedDeletionTest"))

    private companion object {
        val FeedA = UUID("feed-a")
        val FeedB = UUID("feed-b")
        val ArticleA = UUID("article-a")
        val ArticleB = UUID("article-b")
    }
}
