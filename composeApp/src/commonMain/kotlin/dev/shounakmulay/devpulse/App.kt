package dev.shounakmulay.devpulse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.core.designsystem.theme.AppTheme
import dev.shounakmulay.core.navigation.NavDisplay
import dev.shounakmulay.core.navigation.NavigationState
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.core.navigation.rememberNavigationState
import dev.shounakmulay.devpulse.di.koinConfiguration
import dev.shounakmulay.feature.devtools.navigation.developerToolsFeatureEntries
import dev.shounakmulay.feature.feed.navigation.feedFeatureEntries
import dev.shounakmulay.feature.home.navigation.homeFeatureEntries
import org.koin.compose.KoinApplication


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration) {
        val tabRoutes by remember {
            mutableStateOf(
                setOf(
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

        AppTheme {
            Scaffold {
                val isTabsPageVisible by derivedStateOf {
                    navigationState.rootStack.last() == Screen.Tabs
                }

                if (isTabsPageVisible) {
                    NavigationSuiteScaffold(
                        modifier = Modifier.padding(it),
                        navigationItems = {
                            tabRoutes.forEach { tab ->
                                NavigationSuiteItem(
                                    selected = tab == navigationState.selectedTab,
                                    onClick = { navigator.navigate(tab, false) },
                                    icon = {
                                        Icon(
                                            imageVector = when (tab) {
                                                Screen.Tabs.Home -> Icons.Default.Home
                                                Screen.Tabs.Feed -> Icons.Default.DynamicFeed
                                                Screen.Tabs.Time -> Icons.Default.Timer
                                                else -> Icons.Default.ExpandMore
                                            },
                                            contentDescription = ""
                                        )
                                    },
                                    label = {
                                        DPTextView(
                                            text = tab.toString(),
                                            variant = DPTextViewVariant.LabelMedium
                                        )
                                    },
                                )
                            }
                        }
                    ) {
                        NavDisplay(navigationState, navigator)
                    }
                } else {
                    NavDisplay(navigationState, navigator)
                }
            }
        }
    }
}

@Composable
private fun NavDisplay(
    navigationState: NavigationState,
    navigator: Navigator
) {
    NavDisplay(
        modifier = Modifier,
        navigationState = navigationState,
        navigator = navigator,
        entryProvider = {
            developerToolsFeatureEntries(navigator)
            homeFeatureEntries(navigator)
            feedFeatureEntries(navigator)
            entry<Screen.Tabs.Time> {
                ScreenPlaceholder(it, onOpenSettings = {})
            }
            entry<Screen.Settings> {
                ScreenPlaceholder(it, onOpenSettings = {})
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
