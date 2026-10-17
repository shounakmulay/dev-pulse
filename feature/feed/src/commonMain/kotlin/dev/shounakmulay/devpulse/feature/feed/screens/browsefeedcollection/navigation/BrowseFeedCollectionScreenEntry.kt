package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui.BrowseFeedCollectionScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal fun EntryProviderScope<Screen>.browseFeedCollectionScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.BrowseFeedCollection> {
        BrowseFeedCollectionScreen(
            viewModel = koinViewModel { parametersOf(it.collectionId) },
            navigator = navigator,
        )
    }
}
