package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import dev.shounakmulay.devpulse.core.navigation.scene.listDetail.ExpandableListDetailSceneStrategy
import dev.shounakmulay.devpulse.core.navigation.transition.NavTransitionSpecType
import dev.shounakmulay.devpulse.core.navigation.transition.createTransitionSpec
import kotlinx.collections.immutable.PersistentSet

@Composable
fun NavDisplay(
    modifier: Modifier,
    navigationState: NavigationState,
    navigator: Navigator,
    tabRoutes: PersistentSet<Screen>,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navigationSuiteType: NavigationSuiteType,
    entryProvider: EntryProviderScope<Screen>.() -> Unit
) {
    val sceneStrategies by remember(windowAdaptiveInfo) {
        derivedStateOf {
            listOf(
                ExpandableListDetailSceneStrategy(
                    windowSizeClass = windowAdaptiveInfo.windowSizeClass,
                ),
                SinglePaneSceneStrategy<Screen>()
            )
        }
    }
    NavDisplay(
        modifier = modifier,
        transitionSpec = {
            createTransitionSpec(
                scope = this,
                tabRoutes = tabRoutes,
                navigationSuiteType = navigationSuiteType,
                transitionSpecType = NavTransitionSpecType.TRANSITION
            )
        },
        popTransitionSpec = {
            createTransitionSpec(
                scope = this,
                tabRoutes = tabRoutes,
                navigationSuiteType = navigationSuiteType,
                transitionSpecType = NavTransitionSpecType.POP_TRANSITION
            )
        },
        predictivePopTransitionSpec = {
            createTransitionSpec(
                scope = this,
                tabRoutes = tabRoutes,
                navigationSuiteType = navigationSuiteType,
                transitionSpecType = NavTransitionSpecType.PREDICTIVE_POP_TRANSITION
            )
        },
        sceneStrategies = sceneStrategies,
        entries = navigationState.toEntries(entryProvider = entryProvider {
            entryProvider()
        }),
        onBack = {
            navigator.navigateBack()
        }
    )
}
