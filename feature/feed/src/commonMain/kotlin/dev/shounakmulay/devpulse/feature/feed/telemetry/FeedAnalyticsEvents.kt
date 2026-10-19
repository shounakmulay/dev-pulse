package dev.shounakmulay.devpulse.feature.feed.telemetry

import dev.shounakmulay.devpulse.core.telemetry.api.*

data class BookmarkChanged(val bookmarked: Boolean) : AnalyticsEvent {
    override val definition = Definition
    override val attributes = SafeAttributes.of("bookmarked" to AttributeValue.Flag(bookmarked))

    private companion object {
        val Definition = EventDefinition(
            name = "bookmark_changed",
            version = 1,
            owner = "feed",
            attributes = AttributeSchema(mapOf("bookmarked" to AttributeRule.Flag)),
        )
    }
}
