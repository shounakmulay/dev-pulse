package dev.shounakmulay.feature.feed.screens.feed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.feed.screens.feed.ui.FeedScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.feedScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed> {
        FeedScreen(navigator = navigator, viewModel = koinViewModel())
    }
}
