package dev.shounakmulay.feature.feed.screens.feed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.feed.screens.feed.ui.FeedDetailScreen
import dev.shounakmulay.feature.feed.screens.feed.ui.FeedScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.feedScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed>(
        metadata = ExpandableListDetailSceneStrategy.listPane()
    ) {
        FeedScreen(navigator = navigator, viewModel = koinViewModel())
    }
    entry<Screen.Tabs.Feed.FeedDetail>(
        metadata = ExpandableListDetailSceneStrategy.detailPane()
    ) {
        FeedDetailScreen(it)
    }
}
