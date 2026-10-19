package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.navigation.NavigationPaneRole
import dev.shounakmulay.devpulse.core.navigation.NavigationVisibilityObserver
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.remoteconfig.ConfigActivation
import dev.shounakmulay.devpulse.core.remoteconfig.RemoteConfig
import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AppNavigationTelemetry(
    private val analytics: Analytics,
    private val config: RemoteConfig,
    private val scope: ApplicationScope,
    private val observability: AppObservability,
) : NavigationVisibilityObserver {
    private val visibleEntries = MutableStateFlow<Set<String>>(emptySet())

    override fun onVisible(entryId: String, screen: Screen, role: NavigationPaneRole) {
        observability.foreground()
        while (true) {
            val current = visibleEntries.value
            if (entryId in current) return
            if (visibleEntries.compareAndSet(current, current + entryId)) break
        }
        analytics.track(ScreenViewed(screen.safeScreenId(), role.name.lowercase()))
        scope.launch { config.activate(ConfigActivation.NEXT_SCREEN) }
    }

    override fun onHidden(entryId: String) {
        visibleEntries.update { it - entryId }
    }

    private data class ScreenViewed(val screen: String, val pane: String) : AnalyticsEvent {
        override val definition = ScreenDefinition
        override val attributes = SafeAttributes.of(
            "screen" to AttributeValue.Text(screen),
            "pane" to AttributeValue.Text(pane),
        )
    }

    private companion object {
        val ScreenIds = setOf("home", "feed", "feed_list", "post_list", "post_detail", "feed_detail", "add_feed", "browse_feeds", "browse_collection", "image", "time", "settings", "article_settings", "licenses", "design_system", "web_view", "monitors", "tabs")
        val ScreenDefinition = EventDefinition(
            name = "screen_viewed",
            version = 1,
            owner = "application",
            attributes = AttributeSchema(mapOf(
                "screen" to AttributeRule.Choice(ScreenIds),
                "pane" to AttributeRule.Choice(setOf("primary", "list", "detail")),
            )),
        )
    }
}

internal fun Screen.safeScreenId(): String = when (this) {
    Screen.Tabs -> "tabs"
    Screen.Tabs.Home -> "home"
    Screen.Tabs.Feed -> "feed"
    Screen.Tabs.Feed.FeedList -> "feed_list"
    is Screen.Tabs.Feed.PostList -> "post_list"
    is Screen.Tabs.Feed.PostDetail -> "post_detail"
    is Screen.Tabs.Feed.FeedDetail -> "feed_detail"
    Screen.Tabs.Feed.AddFeed -> "add_feed"
    Screen.Tabs.Feed.BrowseFeeds -> "browse_feeds"
    is Screen.Tabs.Feed.BrowseFeedCollection -> "browse_collection"
    is Screen.Tabs.Feed.ImageScreen -> "image"
    Screen.Tabs.Time -> "time"
    Screen.Settings -> "settings"
    Screen.Settings.ArticleSettings -> "article_settings"
    Screen.AboutLibs -> "licenses"
    Screen.DeveloperTools.DesignSystemBoard -> "design_system"
    is Screen.WebView -> "web_view"
    Screen.Monitors -> "monitors"
}
