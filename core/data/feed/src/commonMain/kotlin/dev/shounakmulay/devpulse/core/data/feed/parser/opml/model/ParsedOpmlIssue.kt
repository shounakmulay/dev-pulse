package dev.shounakmulay.devpulse.core.data.feed.parser.opml.model

data class ParsedOpmlIssue(
    val outlinePath: List<String>,
    val attribute: String?,
    val message: String
)
