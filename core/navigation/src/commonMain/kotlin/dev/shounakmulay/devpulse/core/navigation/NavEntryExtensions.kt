package dev.shounakmulay.devpulse.core.navigation

import androidx.navigation3.runtime.NavEntry

internal fun <T : Any> NavEntry<T>.getScreenString(): String {
    val screenString = (contentKey as? Pair<*, *>)?.first as? String
    checkNotNull(screenString) {
        "Screen string not found for $this. This could indicate that something has changed in the navigation library"
    }
    return screenString
}