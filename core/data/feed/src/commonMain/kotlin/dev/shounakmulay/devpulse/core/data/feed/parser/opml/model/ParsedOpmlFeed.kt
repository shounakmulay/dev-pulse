package dev.shounakmulay.devpulse.core.data.feed.parser.opml.model

data class ParsedOpmlFeed(
    val title: String?,
    val text: String?,
    val type: String?,
    val xmlUrl: String,
    val htmlUrl: String?,
    val description: String?,
    val categoryPath: List<String>
)
