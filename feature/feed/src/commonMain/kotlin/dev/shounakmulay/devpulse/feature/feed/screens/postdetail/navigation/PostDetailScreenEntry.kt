package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.PostDetailScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal fun EntryProviderScope<Screen>.postDetailScreen(navigator: Navigator) {
    entry<Screen.Tabs.Feed.PostDetail>(
        metadata = ExpandableListDetailSceneStrategy.detailPane(draggable = true)
    ) {
        PostDetailScreen(
            viewModel = koinViewModel {
                parametersOf(it.id)
            },
            navigator = navigator
        )
    }
}
