package dev.shounakmulay.devpulse.core.navigation.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import dev.shounakmulay.devpulse.core.navigation.Screen
import kotlinx.collections.immutable.PersistentSet

internal fun createTransitionSpec(
    scope: AnimatedContentTransitionScope<Scene<Screen>>,
    tabRoutes: PersistentSet<Screen>,
    navigationSuiteType: NavigationSuiteType,
    transitionSpecType: NavTransitionSpecType,
): ContentTransform {
    val transitionType = evaluateTransitionType(
        scope = scope,
        tabRoutes = tabRoutes,
        navigationSuiteType = navigationSuiteType,
        transitionSpecType = transitionSpecType
    )

    return when (transitionType) {
        is NavTransitionType.PopInStack -> backwardTransitionSpec()
        NavTransitionType.PushInStack -> forwardTransitionSpec()
        is NavTransitionType.TabSwitch -> {
            when (transitionType.direction) {
                TabSwitchDirection.FORWARD -> forwardTransitionSpec()
                TabSwitchDirection.BACKWARD -> backwardTransitionSpec()
                TabSwitchDirection.UPWARD -> upwardTransitionSpec()
                TabSwitchDirection.DOWNWARD -> downwardTransitionSpec()
            }
        }
    }
}

private fun evaluateTransitionType(
    scope: AnimatedContentTransitionScope<Scene<Screen>>,
    tabRoutes: PersistentSet<Screen>,
    navigationSuiteType: NavigationSuiteType,
    transitionSpecType: NavTransitionSpecType,
): NavTransitionType {
    val fromStack = scope.initialState.previousEntries + scope.initialState.entries
    val toStack = scope.targetState.previousEntries + scope.targetState.entries

    val fromTabIndex = fromStack.findRootTabIndex(tabRoutes)
    val toTabIndex = toStack.findRootTabIndex(tabRoutes)

    if (fromTabIndex != null && toTabIndex != null && fromTabIndex != toTabIndex) {
        val isVertical = navigationSuiteType.isSideNavigation()

        val direction = when {
            fromTabIndex > toTabIndex -> {
                if (isVertical) TabSwitchDirection.DOWNWARD else TabSwitchDirection.BACKWARD
            }

            else -> {
                if (isVertical) TabSwitchDirection.UPWARD else TabSwitchDirection.FORWARD
            }

        }

        return NavTransitionType.TabSwitch(
            direction = direction
        )
    }

    return when (transitionSpecType) {
        NavTransitionSpecType.TRANSITION -> NavTransitionType.PushInStack
        NavTransitionSpecType.POP_TRANSITION -> NavTransitionType.PopInStack(predictive = false)
        NavTransitionSpecType.PREDICTIVE_POP_TRANSITION -> NavTransitionType.PopInStack(predictive = true)
    }
}

private fun List<NavEntry<Screen>>.findRootTabIndex(tabRoutes: PersistentSet<Screen>): Int? {
    val tabRouteStrings = tabRoutes.map { it.toString() }
    val tabScreen = lastOrNull { it.contentKey.toString() in tabRouteStrings }

    val result = tabScreen?.let {
        tabRouteStrings.indexOf(it.contentKey.toString())
    }
    return result
}