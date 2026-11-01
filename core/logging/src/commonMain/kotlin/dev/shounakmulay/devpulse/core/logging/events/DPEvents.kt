package dev.shounakmulay.devpulse.core.logging.events

enum class DPEvents(val eventName: String) {
    POST_BOOKMARK_CHANGED("post_bookmark_changed"),
    FEED_DELETED("feed_deleted"),
    OPML_EXTRACTION_COMPLETED("opml_extraction_completed"),
    OPML_EXTRACTION_FAILED("opml_extraction_failed"),
    POST_CONTENT_FETCH_COMPLETED("post_content_fetch_completed"),
    POST_CONTENT_FETCH_FAILED("post_content_fetch_failed"),
    POST_CONTENT_VIEW_FAILED("post_content_view_failed"),
    FEED_IMPORT_COMPLETED("feed_import_completed"),
    FEED_IMPORT_FAILED("feed_import_failed"),
    DISCOVERY_FETCH_COMPLETED("discovery_fetch_completed"),
    DISCOVERY_FETCH_FAILED("discovery_fetch_failed"),
}
