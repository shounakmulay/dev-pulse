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

class NavigatorRemoveFromFirstTest {
    @Test
    fun `Given repeated matching root entries When removing Then the oldest match and entire suffix are removed`() {
        val state = state(
            Screen.Tabs,
            Screen.Settings,
            Screen.Tabs.Feed.FeedDetail(FeedA),
            Screen.Tabs.Feed.PostDetail(ArticleA),
            Screen.Tabs.Feed.FeedDetail(FeedB),
            Screen.Tabs.Feed.FeedDetail(FeedA),
            Screen.WebView("https://example.com"),
        )
        val navigator = navigator(state)

        assertTrue(navigator.removeFeedRoutes(FeedA))

        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
        assertEquals(Screen.Tabs.Feed, state.selectedTab)
        assertFalse(navigator.removeFromFirst(onRootStack = true) { it == Screen.Tabs.Feed.FeedDetail(FeedA) })
        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
        navigator.navigateBack()
        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
    }

    @Test
    fun `Given root and hidden tab history When removing in selected tab Then only that tab is truncated`() {
        val state = state(Screen.Tabs, Screen.Settings)
        state.tabsBackStacks.getValue(Screen.Tabs.Home).add(Screen.Settings)
        state.tabsBackStacks.getValue(Screen.Tabs.Feed).addAll(
            listOf(Screen.Tabs.Feed.FeedList, Screen.Settings, Screen.AboutLibs, Screen.Settings),
        )

        assertTrue(navigator(state).removeFromFirst { it == Screen.Settings })

        assertEquals(listOf(Screen.Tabs.Feed, Screen.Tabs.Feed.FeedList), state.tabsBackStacks.getValue(Screen.Tabs.Feed).toList())
        assertEquals(listOf(Screen.Tabs.Home, Screen.Settings), state.tabsBackStacks.getValue(Screen.Tabs.Home).toList())
        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
        assertEquals(Screen.Tabs.Feed, state.selectedTab)
    }

    @Test
    fun `Given a first root entry match When removing Then false is returned and all navigation stays unchanged`() {
        val state = state(Screen.Settings, Screen.AboutLibs)

        assertFalse(navigator(state).removeFromFirst(onRootStack = true) { it == Screen.Settings })

        assertEquals(listOf(Screen.Settings, Screen.AboutLibs), state.rootStack.toList())
        assertEquals(listOf<Screen>(Screen.Tabs.Feed), state.tabsBackStacks.getValue(Screen.Tabs.Feed).toList())
        assertEquals(Screen.Tabs.Feed, state.selectedTab)
    }

    @Test
    fun `Given a first tab entry match When removing Then the tab is preserved`() {
        val state = state(Screen.Tabs)
        state.tabsBackStacks.getValue(Screen.Tabs.Feed).add(Screen.Settings)

        assertFalse(navigator(state).removeFromFirst { it == Screen.Tabs.Feed })

        assertEquals(listOf(Screen.Tabs.Feed, Screen.Settings), state.tabsBackStacks.getValue(Screen.Tabs.Feed).toList())
        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
    }

    @Test
    fun `Given no matching entry When removing Then false is returned and history is preserved`() {
        val state = state(Screen.Tabs, Screen.Settings)

        assertFalse(navigator(state).removeFromFirst(onRootStack = true) { it == Screen.AboutLibs })
        assertFalse(navigator(state).removeFromFirst { it == Screen.Settings })

        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
        assertEquals(listOf<Screen>(Screen.Tabs.Feed), state.tabsBackStacks.getValue(Screen.Tabs.Feed).toList())
    }

    @Test
    fun `Given only a matching deep link When removing Then false is returned without a fallback route`() {
        val state = state(Screen.Tabs.Feed.FeedDetail(FeedA))

        assertFalse(navigator(state).removeFromFirst(onRootStack = true) { it == Screen.Tabs.Feed.FeedDetail(FeedA) })

        assertEquals(listOf<Screen>(Screen.Tabs.Feed.FeedDetail(FeedA)), state.rootStack.toList())
        assertEquals(Screen.Tabs.Feed, state.selectedTab)
    }

    @Test
    fun `Given restored detail history When removing the feed detail Then its article suffix is removed`() {
        val routes = listOf<Screen>(Screen.Tabs, Screen.Tabs.Feed.FeedDetail(FeedA), Screen.Tabs.Feed.PostDetail(ArticleA))
        val restored = Json.decodeFromString<List<Screen>>(Json.encodeToString(routes))
        val state = state(*restored.toTypedArray())

        assertTrue(navigator(state).removeFromFirst(onRootStack = true) { it == Screen.Tabs.Feed.FeedDetail(FeedA) })

        assertEquals(routes, restored)
        assertEquals(listOf<Screen>(Screen.Tabs), state.rootStack.toList())
    }

    @Test
    fun `Given a final matching entry When removing Then earlier history is retained`() {
        val state = state(Screen.Tabs, Screen.Settings, Screen.AboutLibs)

        assertTrue(navigator(state).removeFromFirst(onRootStack = true) { it == Screen.AboutLibs })

        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
    }

    @Test
    fun `Given no selected tab stack When removing Then false is returned without changing root history`() {
        val state = state(Screen.Tabs, Screen.Settings)
        state.selectedTab = Screen.Tabs.Time

        assertFalse(navigator(state).removeFromFirst { true })

        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
        assertEquals(Screen.Tabs.Time, state.selectedTab)
    }

    @Test
    fun `Given an article before the matching feed detail When removing Then earlier article history is retained`() {
        val state = state(
            Screen.Tabs,
            Screen.Tabs.Feed.PostDetail(ArticleA),
            Screen.Tabs.Feed.FeedDetail(FeedA),
            Screen.Settings,
        )

        assertTrue(navigator(state).removeFromFirst(onRootStack = true) { it == Screen.Tabs.Feed.FeedDetail(FeedA) })

        assertEquals(listOf(Screen.Tabs, Screen.Tabs.Feed.PostDetail(ArticleA)), state.rootStack.toList())
    }

    @Test
    fun `Given a directly opened article When deleting its feed Then the article and all later routes are removed`() {
        val route: Screen = Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA)
        val restored = Json.decodeFromString<Screen>(Json.encodeToString(route))
        val state = state(
            Screen.Tabs,
            Screen.Settings,
            restored,
            Screen.Tabs.Feed.FeedDetail(FeedB),
            Screen.Tabs.Feed.FeedDetail(FeedA),
        )

        assertTrue(navigator(state).removeFeedRoutes(FeedA))

        assertEquals(Screen.Tabs.Feed.PostDetail(id = ArticleA, feedId = FeedA), restored)
        assertEquals(listOf(Screen.Tabs, Screen.Settings), state.rootStack.toList())
    }

    @Test
    fun `Given an older article route When restoring Then absent feed ownership still decodes`() {
        val restored = Json.decodeFromString<Screen.Tabs.Feed.PostDetail>("""{"id":"article-a"}""")

        assertEquals(Screen.Tabs.Feed.PostDetail(ArticleA), restored)
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

    private fun navigator(state: NavigationState) = Navigator(state, DPLog.tag("NavigatorRemoveFromFirstTest"))

    private companion object {
        val FeedA = UUID("feed-a")
        val FeedB = UUID("feed-b")
        val ArticleA = UUID("article-a")
    }
}
