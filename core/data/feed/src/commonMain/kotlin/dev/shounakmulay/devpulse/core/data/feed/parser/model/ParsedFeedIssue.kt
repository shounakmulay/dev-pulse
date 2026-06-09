package dev.shounakmulay.devpulse.core.data.feed.parser.model

data class ParsedFeedIssue(
    val sourceUrl: String?,
    val itemOrdinal: Int?,
    val tag: String?,
    val attribute: String?,
    val message: String
)
