package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedIssue
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItem
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser

abstract class FeedParser {
    abstract suspend fun parse(pullParser: XmlPullParser): ParsedFeed

    protected fun tagNotClosed(
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

    protected fun feedItemsFlow(
        pullParser: XmlPullParser,
        itemTag: String,
        parseItem: () -> ParsedFeedItem?
    ): Flow<ParsedFeedItem> {
        return flow {
            var readCurrentTag = pullParser.eventType == EventType.START_TAG

            while (readCurrentTag || pullParser.next() != EventType.END_DOCUMENT) {
                readCurrentTag = false
                if (pullParser.eventType != EventType.START_TAG) continue

                if (pullParser.name != itemTag) {
                    pullParser.skipSubTree()
                    continue
                }

                val item = parseItem()
                if (item != null) {
                    emit(item)
                }
            }
        }
    }

    protected fun emptyFeed(issues: List<ParsedFeedIssue>): ParsedFeed {
        return ParsedFeed(
            metadata = ParsedFeedMetadata.build {},
            items = emptyFlow(),
            issues = issues
        )
    }

    companion object {

        // region Feed Identifiers
        const val TAG_RDF = "rdf:RDF"
        const val TAG_RSS = "rss"
        const val TAG_ATOM = "feed"
        const val TAG_RSS_CHANNEL = "channel"
        // endregion

        // region Feed Item Identifiers
        const val TAG_RSS_ITEM = "item"
        const val TAG_ATOM_ENTRY = "entry"
        // endregion

        // region Data Tags
        const val TAG_TITLE = "title"
        const val TAG_LINK = "link"
        const val TAG_DESCRIPTION = "description"
        const val TAG_LAST_BUILD_DATE = "lastBuildDate"
        const val TAG_IMAGE = "image"
        const val TAG_URL = "url"
        const val TAG_ITUNES_IMAGE = "itunes:image"
        const val TAG_ID = "id"
        const val TAG_SUBTITLE = "subtitle"
        const val TAG_CONTENT = "content"
        const val TAG_SUMMARY = "summary"
        const val TAG_PUBLISHED = "published"
        const val TAG_UPDATED = "updated"
        const val TAG_DC_DATE = "dc:date"
        const val TAG_ICON = "icon"
        const val TAG_LOGO = "logo"
        const val TAG_NAME = "name"
        const val ATTR_HREF = "href"
        const val TAG_GUID = "guid"
        const val TAG_PUB_DATE = "pubDate"
        const val TAG_ENCLOSURE = "enclosure"
        const val TAG_CONTENT_ENCODED = "content:encoded"
        const val TAG_DC_CREATOR = "dc:creator"
        const val TAG_AUTHOR = "author"
        const val TAG_CATEGORY = "category"
        const val TAG_MEDIA_CONTENT = "media:content"
        // endregion

        // region Attributes
        const val ATTR_URL = "url"
        const val ATTR_TYPE = "type"
        const val ATTR_REL = "rel"
        const val ATTR_RDF_RESOURCE = "rdf:resource"
        const val ATTR_TERM = "term"
        // endregion

        // region Attribute values
        const val ATTR_VAL_AUDIO_PREFIX = "audio/"
        const val ATTR_VAL_IMAGE_PREFIX = "image/"
        const val ATTR_VAL_VIDEO_PREFIX = "video/"
        const val ATTR_MEDIUM = "medium"
        const val ATTR_VAL_IMAGE = "image"
        const val ATTR_VAL_ALTERNATE = "alternate"
        const val ATTR_VAL_ENCLOSURE = "enclosure"

        // endregion
    }
}
