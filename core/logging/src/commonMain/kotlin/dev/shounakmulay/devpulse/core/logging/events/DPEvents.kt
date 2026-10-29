package dev.shounakmulay.devpulse.core.logging.events

enum class DPEvents(val eventName: String) {
    FEED_IMPORT_COMPLETED("feed_import_completed"),
    FEED_IMPORT_FAILED("feed_import_failed"),
    DISCOVERY_FETCH_COMPLETED("discovery_fetch_completed"),
    DISCOVERY_FETCH_FAILED("discovery_fetch_failed"),
}
