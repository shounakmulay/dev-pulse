package dev.shounakmulay.devpulse.core.domain.models.contentSettings

import kotlinx.serialization.Serializable

@Serializable
enum class BrowserOpeningMode {
    IN_APP_BROWSER,
    EXTERNAL_BROWSER,
}
