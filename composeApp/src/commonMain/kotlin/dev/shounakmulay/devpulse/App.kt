package dev.shounakmulay.devpulse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import dev.shounakmulay.core.designsystem.theme.AppTheme
import dev.shounakmulay.core.navigation.NavDisplay
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.core.navigation.rememberNavigationState
import dev.shounakmulay.feature.devtools.navigation.developerToolsScreenEntry


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {

    val navigationState = rememberNavigationState(
        rootStart = Screen.DeveloperTools,
        tabsStart = Screen.Tabs.Home,
        tabRoutes = setOf(Screen.Tabs.Home, Screen.Tabs.Feed, Screen.Tabs.Time)
    )
    val navigator = remember { Navigator(navigationState) }

    AppTheme {
        Scaffold {
            NavDisplay(
                modifier = Modifier,
                navigationState = navigationState,
                navigator = navigator,
                entryProvider = {
                    developerToolsScreenEntry(navigator)
                }
            )
        }
    }
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
