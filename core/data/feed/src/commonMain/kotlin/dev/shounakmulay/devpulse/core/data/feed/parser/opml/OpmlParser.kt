package dev.shounakmulay.devpulse.core.data.feed.parser.opml

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedIssue
import dev.shounakmulay.devpulse.core.data.feed.parser.opml.model.ParsedOpmlDocument
import dev.shounakmulay.devpulse.core.data.feed.parser.opml.model.ParsedOpmlFeed
import dev.shounakmulay.devpulse.core.data.feed.parser.opml.model.ParsedOpmlIssue
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.XmlPullReader
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.sanitized
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.kobjects.ktxml.mini.MiniXmlPullParser
import org.koin.core.annotation.Factory

@Factory
class OpmlParser {
    fun parseText(opml: String): ParsedOpmlDocument {
        val feedIssues = mutableListOf<ParsedFeedIssue>()
        val parser = MiniXmlPullParser(opml.iterator())
        val reader = XmlPullReader(sourceUrl = null, issues = feedIssues)
        val issues = mutableListOf<ParsedOpmlIssue>()
        val feeds = mutableListOf<ParsedOpmlFeed>()
        var title: String? = null
        runCatching {
            while (parser.next() != EventType.END_DOCUMENT) {
                if (parser.eventType == EventType.START_TAG && reader.matches(parser, "opml")) {
                    title = readOpml(parser, reader, feeds, issues)
                }
            }
        }.onFailure { throwable ->
            issues += ParsedOpmlIssue(
                outlinePath = emptyList(),
                attribute = null,
                message = throwable.message ?: "Unable to parse OPML"
            )
        }
        return ParsedOpmlDocument(
            title = title,
            feeds = feeds.mapNotNull { feed ->
                val sanitized = feed.sanitized()
                if (sanitized == null) {
                    issues += ParsedOpmlIssue(
                        outlinePath = feed.categoryPath,
                        attribute = "xmlUrl",
                        message = "Skipped outline with unsupported feed URL"
                    )
                }
                sanitized
            },
            issues = issues
        )
    }

    fun parseFileBytes(fileName: String?, bytes: ByteArray): ParsedOpmlDocument {
        return parseText(bytes.decodeToString())
    }

    private fun readOpml(
        parser: XmlPullParser,
        reader: XmlPullReader,
        feeds: MutableList<ParsedOpmlFeed>,
        issues: MutableList<ParsedOpmlIssue>
    ): String? {
        var title: String? = null
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "head") -> title = title ?: readHead(parser, reader)
                    reader.matches(parser, "body") -> readBody(parser, reader, feeds, issues)
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "opml")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return title
    }

    private fun readHead(parser: XmlPullParser, reader: XmlPullReader): String? {
        var title: String? = null
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "title") -> title = title ?: reader.readElementText(parser)
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "head")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return title
    }

    private fun readBody(
        parser: XmlPullParser,
        reader: XmlPullReader,
        feeds: MutableList<ParsedOpmlFeed>,
        issues: MutableList<ParsedOpmlIssue>
    ) {
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "outline") -> readOutline(parser, reader, feeds, issues, emptyList())
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "body")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
    }

    private fun readOutline(
        parser: XmlPullParser,
        reader: XmlPullReader,
        feeds: MutableList<ParsedOpmlFeed>,
        issues: MutableList<ParsedOpmlIssue>,
        parentPath: List<String>
    ) {
        val title = reader.attribute(parser, "title")
        val text = reader.attribute(parser, "text")
        val type = reader.attribute(parser, "type")
        val xmlUrl = reader.attribute(parser, "xmlUrl", "xmlurl")
        val htmlUrl = reader.attribute(parser, "htmlUrl", "htmlurl")
        val description = reader.attribute(parser, "description")
        val label = title ?: text
        val currentPath = when {
            xmlUrl == null && !label.isNullOrBlank() -> parentPath + label
            else -> parentPath
        }
        if (xmlUrl != null) {
            feeds += ParsedOpmlFeed(
                title = title,
                text = text,
                type = type,
                xmlUrl = xmlUrl,
                htmlUrl = htmlUrl,
                description = description,
                categoryPath = parentPath
            )
        } else if (type == "rss" || htmlUrl != null) {
            issues += ParsedOpmlIssue(
                outlinePath = parentPath,
                attribute = "xmlUrl",
                message = "Skipped outline without xmlUrl"
            )
        }
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "outline") -> readOutline(parser, reader, feeds, issues, currentPath)
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "outline")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
    }
}
