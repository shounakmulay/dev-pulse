package dev.shounakmulay.devpulse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import dev.shounakmulay.core.designsystem.icon.DPIcons
import dev.shounakmulay.core.designsystem.theme.AppTheme
import dev.shounakmulay.devpulse.core.navigation.NavDisplay
import dev.shounakmulay.devpulse.core.navigation.NavigationState
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.rememberNavigationState
import dev.shounakmulay.devpulse.di.koinConfiguration
import dev.shounakmulay.feature.devtools.navigation.developerToolsFeatureEntries
import dev.shounakmulay.feature.feed.navigation.feedFeatureEntries
import dev.shounakmulay.feature.home.navigation.homeFeatureEntries
import dev.shounakmulay.feature.settings.navigation.settingsFeatureEntries
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import org.koin.compose.KoinApplication


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration) {
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
        val windowAdaptiveInfo = currentWindowAdaptiveInfo()
        val navigationSuiteLayoutType =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

        AppTheme {
            Scaffold {
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

                val haptic = LocalHapticFeedback.current
                NavigationSuiteScaffold(
                    state = navigationSuiteState,
                    navigationItems = {
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
                ) {
                    NavDisplay(
                        navigationState = navigationState,
                        navigator = navigator,
                        tabRoutes = tabRoutes,
                        windowAdaptiveInfo = windowAdaptiveInfo,
                        navigationSuiteType = navigationSuiteLayoutType
                    )
                }
            }
        }
    }
}

@Composable
private fun NavDisplay(
    navigationState: NavigationState,
    navigator: Navigator,
    tabRoutes: PersistentSet<Screen>,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navigationSuiteType: NavigationSuiteType,
) {
    NavDisplay(
        modifier = Modifier,
        navigationState = navigationState,
        navigator = navigator,
        tabRoutes = tabRoutes,
        windowAdaptiveInfo = windowAdaptiveInfo,
        navigationSuiteType = navigationSuiteType,
        entryProvider = {
            developerToolsFeatureEntries(navigator)
            homeFeatureEntries(navigator)
            feedFeatureEntries(navigator)
            settingsFeatureEntries(navigator)
            entry<Screen.Tabs.Time> {
                ScreenPlaceholder(
                    it,
                    onOpenSettings = {
                        navigator.navigate(
                            Screen.Settings,
                            true
                        )
                    })
            }
        }
    )
}


@Composable
fun ScreenPlaceholder(
    screen: NavKey,
    onOpenSettings: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            IconButton(
                onClick = onOpenSettings,
            ) {
                Icon(Icons.Default.Settings, contentDescription = "")
            }
        }
        Text(screen.toString())
    }
}
