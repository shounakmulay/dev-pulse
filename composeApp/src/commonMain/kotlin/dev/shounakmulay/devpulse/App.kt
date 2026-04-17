package dev.shounakmulay.devpulse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.shounakmulay.core.designsystem.theme.AppTheme
import dev.shounakmulay.core.navigation.NavigationState
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.core.navigation.rememberNavigationState


val bottomBarScreens = setOf(Screen.HomeScreen, Screen.FeedScreen, Screen.TimeScreen)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    val navigationState = rememberNavigationState(
        rootStart = Screen.TabsScreen,
        tabsStart = Screen.HomeScreen,
        tabRoutes = bottomBarScreens
    )

    val navigator = remember { Navigator(navigationState) }
    val isTabsScreen = navigationState.rootStack.last() == Screen.TabsScreen
    AppTheme {
        if (!isTabsScreen) {
            Scaffold {
                NavDisplay(it, navigationState, navigator)
            }
            return@AppTheme
        }


        Scaffold(
            bottomBar = {
                BottomAppBar() {
                    bottomBarScreens.forEach {
                        NavigationBarItem(
                            selected = it == navigationState.selectedTab,
                            onClick = { navigator.navigate(it, false) },
                            icon = {},
                            label = {
                                Text(it.toString())
                            }
                        )
                    }
                }
            }
        ) {
            NavDisplay(it, navigationState, navigator)
        }
    }
}

@Composable
private fun NavDisplay(
    values: PaddingValues,
    navigationState: NavigationState,
    navigator: Navigator
) {
    NavDisplay(
        modifier = Modifier.padding(values),
        entries = navigationState.toEntries(entryProvider = entryProvider {
            entry<Screen.HomeScreen> {
                ScreenPlaceholder(
                    it, onOpenSettings = {
                        navigator.navigate(Screen.SettingsScreen, true)
                    },
                    onOpenFeed = {
                        navigator.navigate(Screen.MonitorsScreen, false)
                    },
                    onOpenTime = {
                        navigator.navigate(Screen.TimeScreen, false)
                    }
                )
            }
            entry<Screen.FeedScreen> {
                ScreenPlaceholder(
                    it, onOpenSettings = {
                        navigator.navigate(Screen.SettingsScreen, true)
                    },
                    onOpenFeed = {
                        navigator.navigate(Screen.MonitorsScreen, false)
                    },
                    onOpenTime = {
                        navigator.navigate(Screen.TimeScreen, false)
                    }
                )
            }
            entry<Screen.TimeScreen> {
                ScreenPlaceholder(
                    it, onOpenSettings = {
                        navigator.navigate(Screen.SettingsScreen, true)
                    },
                    onOpenFeed = {
                        navigator.navigate(Screen.MonitorsScreen, false)
                    },
                    onOpenTime = {
                        navigator.navigate(Screen.TimeScreen, false)
                    }
                )
            }
            entry<Screen.SettingsScreen> {
                ScreenPlaceholder(
                    it, onOpenSettings = {
                        navigator.navigate(Screen.SettingsScreen, true)
                    },
                    onOpenFeed = {
                        navigator.navigate(Screen.MonitorsScreen, false)
                    },
                    onOpenTime = {
                        navigator.navigate(Screen.TimeScreen, false)
                    }
                )
            }
            entry<Screen.MonitorsScreen> {
                ScreenPlaceholder(it, {}, {}, {})
            }
        }),
        onBack = { navigator.navigateBack() }
    )
}

@Composable
fun ScreenPlaceholder(
    screen: NavKey,
    onOpenSettings: () -> Unit,
    onOpenFeed: () -> Unit,
    onOpenTime: () -> Unit
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

            IconButton(
                onClick = onOpenFeed,
            ) {
                Icon(Icons.Default.DynamicFeed, contentDescription = "")
            }
            IconButton(
                onClick = onOpenTime,
            ) {
                Icon(Icons.Default.PunchClock, contentDescription = "")
            }
        }
        Text(screen.toString())
    }
}
