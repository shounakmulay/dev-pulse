package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.snapshots.Snapshot
import dev.shounakmulay.devpulse.core.domain.models.common.UUID

fun Navigator.removeFeedRoutes(feedId: UUID): Boolean = Snapshot.withMutableSnapshot {
    val belongsToFeed: (Screen) -> Boolean = { screen ->
        when (screen) {
            is Screen.Tabs.Feed.FeedDetail -> screen.id == feedId
            is Screen.Tabs.Feed.PostDetail -> screen.feedId == feedId
            else -> false
        }
    }
    var changed = state.rootStack.removeAll(belongsToFeed)
    state.tabsBackStacks.forEach { (_, stack) ->
        if (stack.removeAll(belongsToFeed)) changed = true
    }
    if (state.rootStack.isEmpty()) {
        state.rootStack.add(Screen.Tabs)
        state.selectedTab = state.tabsStart
    }
    changed
}
