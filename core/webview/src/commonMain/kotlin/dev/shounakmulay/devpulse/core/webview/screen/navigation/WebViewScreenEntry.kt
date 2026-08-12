package dev.shounakmulay.devpulse.core.webview.screen.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.webview.screen.WebViewScreen

fun EntryProviderScope<Screen>.webViewScreens(navigator: Navigator) {
    entry<Screen.WebView> {
        WebViewScreen(url = it.url, navigator = navigator)
    }
}
