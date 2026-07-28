package dev.shounakmulay.devpulse.feature.settings.screens.aboutLibs.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.aboutLibs.AboutLibsScreen

internal fun EntryProviderScope<Screen>.aboutLibsScreen(navigator: Navigator) {
    entry<Screen.AboutLibs> {
        AboutLibsScreen(navigator = navigator)
    }
}
