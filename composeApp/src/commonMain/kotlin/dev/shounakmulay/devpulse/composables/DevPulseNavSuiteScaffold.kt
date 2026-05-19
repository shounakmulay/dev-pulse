package dev.shounakmulay.devpulse.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.NavigationState
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import kotlinx.collections.immutable.PersistentSet

@Composable
internal fun DevPulseNavSuiteScaffold(
    navigationSuiteState: NavigationSuiteScaffoldState,
    tabRoutes: PersistentSet<Screen>,
    navigationState: NavigationState,
    navigator: Navigator,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navigationSuiteLayoutType: NavigationSuiteType
) {
    val haptic = LocalHapticFeedback.current
    NavigationSuiteScaffold(
        state = navigationSuiteState,
        navigationItems = {
            NavigationSuiteTabs(tabRoutes, navigationState, haptic, navigator)
        }
    ) {
        DevPulseNavDisplay(
            navigationState = navigationState,
            navigator = navigator,
            tabRoutes = tabRoutes,
            windowAdaptiveInfo = windowAdaptiveInfo,
            navigationSuiteType = navigationSuiteLayoutType
        )
    }
}

@Composable
private fun NavigationSuiteTabs(
    tabRoutes: PersistentSet<Screen>,
    navigationState: NavigationState,
    haptic: HapticFeedback,
    navigator: Navigator
) {
    tabRoutes.forEach { tab ->
        NavigationSuiteItem(
            selected = tab == navigationState.selectedTab,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                navigator.navigate(tab, false)
            },
            icon = {
                Icon(
                    imageVector = when (tab) {
                        Screen.Tabs.Home -> DPIcons.devPulseIconSmall()
                        Screen.Tabs.Feed -> Icons.Default.SpaceDashboard
                        Screen.Tabs.Time -> Icons.Default.Timer
                        else -> Icons.Default.ExpandMore
                    },
                    contentDescription = "",
                    tint = if (tab == Screen.Tabs.Home) Color.Unspecified else LocalContentColor.current
                )
            },
            label = null,
        )
    }
}