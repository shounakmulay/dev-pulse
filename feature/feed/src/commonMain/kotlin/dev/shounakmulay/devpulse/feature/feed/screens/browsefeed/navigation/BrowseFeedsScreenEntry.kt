package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui.BrowseFeedsScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.browseFeedsScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.BrowseFeeds> {
        BrowseFeedsScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
