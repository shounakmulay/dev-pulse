package dev.shounakmulay.devpulse.feature.feed.screens.postlist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.PostListScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.postListScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.PostList> {
        PostListScreen(
            screen = it,
            navigator = navigator,
        )
    }
}
