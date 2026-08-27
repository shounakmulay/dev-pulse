package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedImage
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItem
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedMetadata
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.koin.core.annotation.Factory

@Factory
class RdfFeedParser : FeedParser() {

    override suspend fun parse(pullParser: XmlPullParser): ParsedFeed {
        val feedMetadataBuilder = ParsedFeedMetadata.Companion.Builder()

        while (tagNotClosed(pullParser = pullParser, tag = TAG_RDF)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_RSS_CHANNEL -> parseChannel(
                    pullParser = pullParser,
                    feedMetadataBuilder = feedMetadataBuilder
                )

                TAG_RSS_ITEM -> break
                else -> pullParser.skipSubTree()
            }
        }

        return ParsedFeed(
            metadata = feedMetadataBuilder.build(),
            items = feedItemsFlow(pullParser = pullParser, itemTag = TAG_RSS_ITEM) {
                parseItem(pullParser)
            },
            issues = listOf()
        )
    }

    private fun parseChannel(
        pullParser: XmlPullParser,
        feedMetadataBuilder: ParsedFeedMetadata.Companion.Builder
    ) {
        while (tagNotClosed(pullParser = pullParser, tag = TAG_RSS_CHANNEL)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_TITLE -> {
                    feedMetadataBuilder.title = pullParser.nextText()
                }

                TAG_LINK -> {
                    val link = pullParser.nextText()
                    feedMetadataBuilder.link = feedMetadataBuilder.link ?: link
                }

                TAG_DESCRIPTION -> {
                    feedMetadataBuilder.description = pullParser.nextText()
                }

                TAG_IMAGE -> {
                    feedMetadataBuilder.image = ParsedFeedImage(
                        title = null,
                        url = pullParser.getAttributeValue(pullParser.namespace, ATTR_RDF_RESOURCE),
                        link = null,
                        description = null
                    )
                }

                else -> {
                    pullParser.skipSubTree()
                }
            }
        }
    }

    private fun parseItem(pullParser: XmlPullParser): ParsedFeedItem {
        return ParsedFeedItem.build {
            while (tagNotClosed(pullParser = pullParser, tag = TAG_RSS_ITEM)) {
                if (pullParser.eventType != EventType.START_TAG) continue

                when (pullParser.name) {
                    TAG_TITLE -> title = pullParser.nextText()
                    TAG_LINK -> {
                        val parsedLink = pullParser.nextText()
                        link = link ?: parsedLink
                    }

                    TAG_DESCRIPTION -> description = pullParser.nextText()
                    TAG_CONTENT_ENCODED -> content = pullParser.nextText()
                    TAG_PUB_DATE -> pubDate = pullParser.nextText()
                    TAG_DC_DATE -> {
                        val parsedDate = pullParser.nextText()
                        pubDate = pubDate ?: parsedDate
                    }

                    else -> pullParser.skipSubTree()
                }
            }
        }
    }
}
