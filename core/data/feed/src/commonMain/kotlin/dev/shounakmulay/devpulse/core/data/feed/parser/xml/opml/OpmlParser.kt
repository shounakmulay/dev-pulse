package dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml

import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.model.ParsedOpmlDocument
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.opml.model.ParsedOpmlFeed
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.kobjects.ktxml.mini.MiniXmlPullParser
import org.koin.core.annotation.Factory

@Factory
class OpmlParser {
    fun parse(xmlIterator: CharIterator): ParsedOpmlDocument? {
        val pullParser = MiniXmlPullParser(
            source = xmlIterator,
            relaxed = true,
        )

        while (pullParser.next() != EventType.END_DOCUMENT) {
            if (pullParser.eventType != EventType.START_TAG) continue

            return when (pullParser.name) {
                TAG_OPML -> parseOpml(pullParser)
                else -> null
            }
        }

        return null
    }

    private fun parseOpml(pullParser: XmlPullParser): ParsedOpmlDocument {
        var title: String? = null
        var feeds = emptyList<ParsedOpmlFeed>()

        while (tagNotClosed(pullParser = pullParser, tag = TAG_OPML)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_HEAD -> {
                    title = title ?: parseHead(pullParser)
                }

                TAG_BODY -> {
                    feeds = parseBody(pullParser)
                }

                else -> pullParser.skipSubTree()
            }
        }

        return ParsedOpmlDocument(
            title = title,
            feeds = feeds
        )
    }

    private fun parseHead(pullParser: XmlPullParser): String? {
        var title: String? = null

        while (tagNotClosed(pullParser = pullParser, tag = TAG_HEAD)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_TITLE -> title = title ?: pullParser.nextText()
                else -> pullParser.skipSubTree()
            }
        }

        return title
    }

    private fun parseBody(
        pullParser: XmlPullParser
    ): List<ParsedOpmlFeed> {
        val feeds = mutableListOf<ParsedOpmlFeed>()

        while (tagNotClosed(pullParser = pullParser, tag = TAG_BODY)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_OUTLINE -> {
                    readOutline(pullParser = pullParser)?.let(feeds::add)
                }

                else -> pullParser.skipSubTree()
            }
        }

        return feeds
    }

    private fun readOutline(
        pullParser: XmlPullParser,
    ): ParsedOpmlFeed? {
        val title = pullParser.getAttributeValue(pullParser.namespace, ATTR_TITLE)
        val text = pullParser.getAttributeValue(pullParser.namespace, ATTR_TEXT)
        val type = pullParser.getAttributeValue(pullParser.namespace, ATTR_TYPE)
        val xmlUrl = pullParser.getAttributeValue(pullParser.namespace, ATTR_XML_URL)
            ?: pullParser.getAttributeValue(pullParser.namespace, ATTR_XML_URL.lowercase())
        val htmlUrl = pullParser.getAttributeValue(pullParser.namespace, ATTR_HTML_URL)
            ?: pullParser.getAttributeValue(pullParser.namespace, ATTR_HTML_URL.lowercase())
        val description = pullParser.getAttributeValue(pullParser.namespace, ATTR_DESCRIPTION)

        if (xmlUrl.isNullOrBlank()) {
            return null
        }

        return ParsedOpmlFeed(
            title = title,
            text = text,
            type = type,
            xmlUrl = xmlUrl,
            htmlUrl = htmlUrl,
            description = description,
        )
    }

    private fun tagNotClosed(
        pullParser: XmlPullParser,
        tag: String,
        namespace: String? = pullParser.namespace
    ): Boolean {
        pullParser.next()
        val checkTagResult = runCatching {
            pullParser.require(
                type = EventType.END_TAG,
                namespace = namespace,
                name = tag
            )
        }

        return checkTagResult.isFailure
    }

    private companion object {
        const val TAG_OPML = "opml"
        const val TAG_HEAD = "head"
        const val TAG_BODY = "body"
        const val TAG_TITLE = "title"
        const val TAG_OUTLINE = "outline"
        const val ATTR_TITLE = "title"
        const val ATTR_TEXT = "text"
        const val ATTR_TYPE = "type"
        const val ATTR_XML_URL = "xmlUrl"
        const val ATTR_HTML_URL = "htmlUrl"
        const val ATTR_DESCRIPTION = "description"
    }
}
