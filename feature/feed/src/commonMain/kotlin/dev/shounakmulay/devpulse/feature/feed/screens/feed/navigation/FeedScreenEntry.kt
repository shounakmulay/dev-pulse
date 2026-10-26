package dev.shounakmulay.devpulse.feature.feed.screens.feed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.ScreenKey
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.FeedScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.feedScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed>(
        metadata = metadata {
            put(ScreenKey, Screen.Tabs.Feed)
        }
    ) {
        FeedScreen(navigator = navigator, viewModel = koinViewModel())
    }
}
