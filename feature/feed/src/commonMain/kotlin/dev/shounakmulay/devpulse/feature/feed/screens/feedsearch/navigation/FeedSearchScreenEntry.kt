package dev.shounakmulay.devpulse.feature.feed.screens.feedsearch.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.feedsearch.ui.FeedSearchScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.feedSearchScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.FeedSearch> {
        FeedSearchScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
