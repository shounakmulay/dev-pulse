package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedIssue
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser

internal class XmlPullReader(
    private val sourceUrl: String?,
    private val mutableIssues: MutableList<ParsedFeedIssue>
) {
    val issues: List<ParsedFeedIssue>
        get() = mutableIssues

    fun tagKey(parser: XmlPullParser): String {
        val prefix = parser.prefix.takeIf { it.isNotBlank() }
        return when (prefix) {
            null -> parser.name
            else -> "$prefix:${parser.name}"
        }
    }

    fun matches(parser: XmlPullParser, vararg names: String): Boolean {
        val key = tagKey(parser).lowercase()
        val local = parser.name.lowercase()
        return names.any { candidate ->
            val normalized = candidate.lowercase()
            normalized == key || normalized == local || normalized.substringAfter(':') == local
        }
    }

    fun attribute(parser: XmlPullParser, vararg names: String): String? {
        for (name in names) {
            parser.getAttributeValue("", name)?.let { return it }
            parser.getAttributeValue(parser.namespace, name)?.let { return it }
        }
        for (index in 0 until parser.attributeCount) {
            val attributeName = parser.getAttributeName(index)
            val attributeKey = buildString {
                val prefix = parser.getAttributePrefix(index)
                if (prefix.isNotBlank()) append(prefix).append(':')
                append(attributeName)
            }.lowercase()
            val local = attributeName.lowercase()
            if (names.any { it.lowercase() == attributeKey || it.lowercase().substringAfter(':') == local }) {
                return parser.getAttributeValue(index)
            }
        }
        return null
    }

    fun readElementText(parser: XmlPullParser, itemOrdinal: Int? = null): String? {
        val startTag = tagKey(parser)
        return runCatching {
            val startDepth = parser.depth
            val output = StringBuilder()
            var finished = false
            while (!finished) {
                when (parser.next()) {
                    EventType.TEXT -> output.append(parser.text)
                    EventType.END_TAG -> finished = parser.depth == startDepth && matches(parser, startTag)
                    EventType.END_DOCUMENT -> finished = true
                    else -> Unit
                }
            }
            output.toString().takeIf { it.isNotBlank() }
        }.onFailure { throwable ->
            mutableIssues += ParsedFeedIssue(
                sourceUrl = sourceUrl,
                itemOrdinal = itemOrdinal,
                tag = startTag,
                attribute = null,
                message = throwable.message ?: "Unable to read element text"
            )
        }.getOrNull()
    }

    fun skipCurrent(parser: XmlPullParser, itemOrdinal: Int? = null) {
        val startTag = tagKey(parser)
        runCatching {
            parser.skipSubTree()
        }.onFailure { throwable ->
            mutableIssues += ParsedFeedIssue(
                sourceUrl = sourceUrl,
                itemOrdinal = itemOrdinal,
                tag = startTag,
                attribute = null,
                message = throwable.message ?: "Unable to skip element"
            )
        }
    }
}
