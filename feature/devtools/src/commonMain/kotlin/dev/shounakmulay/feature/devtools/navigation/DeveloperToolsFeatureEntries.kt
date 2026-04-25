package dev.shounakmulay.feature.devtools.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen

fun EntryProviderScope<Screen>.developerToolsFeatureEntries(navigator: Navigator) {
    designSystemBoard(navigator)
}