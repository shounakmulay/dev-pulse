package dev.shounakmulay.devpulse.feature.feed.components.browser

import androidx.compose.ui.platform.UriHandler
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenBrowserTest {
    @Test
    fun `Given external browser choice When opening Then the external handler receives the URL`() {
        val handler = Handler()
        val inApp = mutableListOf<String>()
        openBrowser(target = BrowserOpeningTarget.External("https://example.com/article"), uriHandler = handler, openInApp = inApp::add)
        assertEquals(listOf("https://example.com/article"), handler.opened)
        assertEquals(emptyList(), inApp)
    }

    @Test
    fun `Given no external handler When opening Then the same URL opens in-app`() {
        val handler = Handler().apply { unavailable = true }
        val inApp = mutableListOf<String>()
        openBrowser(target = BrowserOpeningTarget.External("https://example.com/article"), uriHandler = handler, openInApp = inApp::add)
        assertEquals(listOf("https://example.com/article"), inApp)
    }

    @Test
    fun `Given in-app browser choice When opening Then no external handler is called`() {
        val handler = Handler()
        val inApp = mutableListOf<String>()
        openBrowser(target = BrowserOpeningTarget.InApp("https://example.com/article"), uriHandler = handler, openInApp = inApp::add)
        assertEquals(listOf("https://example.com/article"), inApp)
        assertEquals(emptyList(), handler.opened)
    }

    private class Handler : UriHandler {
        val opened = mutableListOf<String>()
        var unavailable = false
        override fun openUri(uri: String) {
            if (unavailable) throw IllegalArgumentException("No handler")
            opened += uri
        }
    }
}
