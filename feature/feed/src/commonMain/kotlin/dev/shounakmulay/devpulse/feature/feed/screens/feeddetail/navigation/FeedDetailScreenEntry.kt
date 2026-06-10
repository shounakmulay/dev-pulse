package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.FeedDetailScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal fun EntryProviderScope<Screen>.feedDetailScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.FeedDetail>(
        metadata = ExpandableListDetailSceneStrategy.detailPane(draggable = true)
    ) {
        FeedDetailScreen(
            route = it,
            navigator = navigator,
            viewModel = koinViewModel(
                parameters = { parametersOf(it.id) }
            )
        )
    }
}
