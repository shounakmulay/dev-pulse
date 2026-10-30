package dev.shounakmulay.devpulse.feature.feed.components.browser

import androidx.compose.ui.platform.UriHandler
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget

internal fun openBrowser(
    target: BrowserOpeningTarget,
    uriHandler: UriHandler,
    openInApp: (String) -> Unit,
) {
    when (target) {
        is BrowserOpeningTarget.InApp -> openInApp(target.url)

        is BrowserOpeningTarget.External -> {
            try {
                uriHandler.openUri(target.url)
            } catch (_: IllegalArgumentException) {
                openInApp(target.url)
            }
        }
    }
}
