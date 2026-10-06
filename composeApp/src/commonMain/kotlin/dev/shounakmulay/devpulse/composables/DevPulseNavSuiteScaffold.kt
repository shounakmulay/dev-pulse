package dev.shounakmulay.devpulse.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.shounakmulay.devpulse.core.designsystem.components.DPSnackbarHost
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.NavigationState
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.feedback.SnackbarController
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
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val snackbarController = remember(snackbarHostState, snackbarScope) {
        SnackbarController(hostState = snackbarHostState, coroutineScope = snackbarScope)
    }
    NavigationSuiteScaffold(
        state = navigationSuiteState,
        navigationItems = {
            NavigationSuiteTabs(
                tabRoutes = tabRoutes,
                navigationState = navigationState,
                haptic = haptic,
                navigator = navigator
            )
        }
    ) {
        CompositionLocalProvider(LocalSnackbarController provides snackbarController) {
            Box(Modifier.fillMaxSize()) {
                DevPulseNavDisplay(
                    navigationState = navigationState,
                    navigator = navigator,
                    tabRoutes = tabRoutes,
                    windowAdaptiveInfo = windowAdaptiveInfo,
                    navigationSuiteType = navigationSuiteLayoutType
                )
                DPSnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
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
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)

                if (tab == navigationState.selectedTab) {
                    navigator.onReselect(tab)
                    return@NavigationSuiteItem
                }

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
