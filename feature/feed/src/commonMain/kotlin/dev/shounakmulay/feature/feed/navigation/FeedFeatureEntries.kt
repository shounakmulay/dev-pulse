package dev.shounakmulay.feature.feed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.core.navigation.Navigator
import dev.shounakmulay.core.navigation.Screen
import dev.shounakmulay.feature.feed.screens.feed.navigation.feedScreen

fun EntryProviderScope<Screen>.feedFeatureEntries(navigator: Navigator) {
    feedScreen(navigator)
}
