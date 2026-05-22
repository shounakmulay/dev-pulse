package dev.shounakmulay.devpulse.composables

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.navigation.NavDisplay
import dev.shounakmulay.devpulse.core.navigation.NavigationState
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.devtools.navigation.developerToolsFeatureEntries
import dev.shounakmulay.devpulse.feature.feed.navigation.feedFeatureEntries
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.FeedQueueViewModel
import dev.shounakmulay.devpulse.feature.home.navigation.homeFeatureEntries
import dev.shounakmulay.devpulse.feature.settings.navigation.settingsFeatureEntries
import kotlinx.collections.immutable.PersistentSet
import org.koin.compose.koinInject

@Composable
internal fun DevPulseNavDisplay(
    navigationState: NavigationState,
    navigator: Navigator,
    tabRoutes: PersistentSet<Screen>,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navigationSuiteType: NavigationSuiteType,
) {
    AppLevelInitialisers()

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
fun AppLevelInitialisers() {
    val feedQueueViewModel: FeedQueueViewModel = koinInject()
    val logger = koinInject<DPLogger>().withTag("DevPulseApp")

    LaunchedEffect(Unit) {
        logger.i { "Starting app-level feed queue initialiser" }
        feedQueueViewModel.init()
    }
}
