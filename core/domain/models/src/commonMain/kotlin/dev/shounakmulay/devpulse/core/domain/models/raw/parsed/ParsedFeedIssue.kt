package dev.shounakmulay.devpulse.core.domain.models.raw.parsed

data class ParsedFeedIssue(
    val sourceUrl: String?,
    val itemOrdinal: Int?,
    val tag: String?,
    val attribute: String?,
    val message: String
)
