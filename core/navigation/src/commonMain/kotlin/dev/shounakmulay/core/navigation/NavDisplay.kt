package dev.shounakmulay.core.navigation

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import dev.shounakmulay.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy

@Composable
fun NavDisplay(
    modifier: Modifier,
    navigationState: NavigationState,
    navigator: Navigator,
    entryProvider: EntryProviderScope<Screen>.() -> Unit
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val sceneStrategies = remember {
        listOf(
            ExpandableListDetailSceneStrategy(
                windowSizeClass = windowAdaptiveInfo.windowSizeClass,
            ),
            SinglePaneSceneStrategy<Screen>()
        )
    }
    NavDisplay(
        modifier = modifier,
        sceneStrategies = sceneStrategies,
        entries = navigationState.toEntries(entryProvider = entryProvider {
            entryProvider()
        }),
        onBack = { navigator.navigateBack() }
    )
}