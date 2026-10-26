package dev.shounakmulay.devpulse.core.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get

data object ScreenKey : NavMetadataKey<Screen>

internal val NavEntry<Screen>.screen: Screen?
    get() = metadata[ScreenKey]
