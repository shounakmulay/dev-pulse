package dev.shounakmulay.devpulse.core.domain.models.contentSettings

sealed interface BrowserOpeningTarget {
    val url: String

    data class InApp(override val url: String) : BrowserOpeningTarget
    data class External(override val url: String) : BrowserOpeningTarget
}
