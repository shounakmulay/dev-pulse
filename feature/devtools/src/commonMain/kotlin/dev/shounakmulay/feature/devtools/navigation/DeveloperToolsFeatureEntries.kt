package dev.shounakmulay.feature.devtools.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen

fun EntryProviderScope<Screen>.developerToolsFeatureEntries(navigator: Navigator) {
    developerToolsScreenEntry(navigator)
    designSystemBoard(navigator)
}