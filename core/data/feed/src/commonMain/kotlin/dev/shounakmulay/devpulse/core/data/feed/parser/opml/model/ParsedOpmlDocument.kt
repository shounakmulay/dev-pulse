package dev.shounakmulay.devpulse.core.data.feed.parser.opml.model

data class ParsedOpmlDocument(
    val title: String?,
    val feeds: List<ParsedOpmlFeed>,
    val issues: List<ParsedOpmlIssue>
)
