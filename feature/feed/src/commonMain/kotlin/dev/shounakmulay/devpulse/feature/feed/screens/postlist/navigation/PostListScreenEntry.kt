package dev.shounakmulay.devpulse.feature.feed.screens.postlist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.PostListScreen

internal fun EntryProviderScope<Screen>.postListScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.PostList>(
        metadata = ExpandableListDetailSceneStrategy.listPane()
    ) {
        PostListScreen(
            screen = it,
            navigator = navigator,
        )
    }
}
