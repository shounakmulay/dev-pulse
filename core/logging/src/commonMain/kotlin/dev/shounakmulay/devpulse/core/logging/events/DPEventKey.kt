package dev.shounakmulay.devpulse.core.logging.events

enum class DPEventKey(val wireName: String) {
    SOURCE("source"),
    BOOKMARKED("bookmarked"),
    ARTICLE_ORIGIN("article_origin"),
    ENVIRONMENT("environment"),
}
