package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.FeedListScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.feedListScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.FeedList>(
        metadata = ExpandableListDetailSceneStrategy.listPane()
    ) {
        FeedListScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
