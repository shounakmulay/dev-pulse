package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.AddFeedScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.addFeedScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.AddFeed>(
        metadata = ExpandableListDetailSceneStrategy.detailPane(draggable = true)
    ) {
        AddFeedScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}
