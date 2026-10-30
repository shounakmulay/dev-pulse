package dev.shounakmulay.devpulse.core.navigation

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

fun Navigator.removeFeedRoutes(feedId: UUID): Boolean = removeFromFirst(onRootStack = true) { screen ->
    when (screen) {
        is Screen.Tabs.Feed.FeedDetail -> screen.id == feedId
        is Screen.Tabs.Feed.PostDetail -> screen.feedId == feedId
        else -> false
    }
}
