package dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.model

data class ParsedOpmlDocument(
    val title: String?,
    val feeds: List<ParsedOpmlFeed>
)
