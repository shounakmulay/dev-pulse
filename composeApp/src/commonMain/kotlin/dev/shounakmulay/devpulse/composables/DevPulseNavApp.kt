package dev.shounakmulay.devpulse.composables

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.rememberNavigationState
import kotlinx.collections.immutable.persistentSetOf

@Composable
fun DevPulseNavApp() {
    val tabRoutes by remember {
        mutableStateOf(
            persistentSetOf(
                Screen.Tabs.Home,
                Screen.Tabs.Feed,
                Screen.Tabs.Time
            )
        )
    }

    val navigationState = rememberNavigationState(
        rootStart = Screen.Tabs,
        tabsStart = Screen.Tabs.Home,
        tabRoutes = tabRoutes
    )
    val navigator = remember { Navigator(navigationState) }
    val navigationSuiteState = rememberNavigationSuiteScaffoldState()
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val navigationSuiteLayoutType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

    val isTabsPageVisible by remember(navigationState.rootStack) {
        derivedStateOf {
            navigationState.rootStack.last() == Screen.Tabs
        }
    }
    LaunchedEffect(isTabsPageVisible) {
        if (isTabsPageVisible) {
            navigationSuiteState.show()
        } else {
            navigationSuiteState.hide()
        }
    }

    DevPulseNavSuiteScaffold(
        navigationSuiteState = navigationSuiteState,
        tabRoutes = tabRoutes,
        navigationState = navigationState,
        navigator = navigator,
        windowAdaptiveInfo = windowAdaptiveInfo,
        navigationSuiteLayoutType = navigationSuiteLayoutType
    )
}