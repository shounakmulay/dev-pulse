package dev.shounakmulay.devpulse.feature.feed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.navigation.addFeedScreen
import dev.shounakmulay.devpulse.feature.feed.screens.feed.navigation.feedScreen
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.navigation.feedListScreen

fun EntryProviderScope<Screen>.feedFeatureEntries(navigator: Navigator) {
    feedScreen(navigator)
    feedListScreen(navigator)
    addFeedScreen(navigator)
}
