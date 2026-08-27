package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedImage
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItem
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedMetadata
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.koin.core.annotation.Factory

@Factory
class RssFeedParser : FeedParser() {

    override suspend fun parse(pullParser: XmlPullParser): ParsedFeed {
        pullParser.nextTag()
        pullParser.require(EventType.START_TAG, pullParser.namespace, TAG_RSS_CHANNEL)

        val feedMetadataBuilder = ParsedFeedMetadata.Companion.Builder()

// TODO:
//        var updatePeriod: String? = null
//        var youtubeChannelId: String? = null

        while (tagNotClosed(
                pullParser = pullParser,
                tag = TAG_RSS_CHANNEL,
            )
        ) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_TITLE -> {
                    feedMetadataBuilder.title = pullParser.nextText()
                }

                TAG_LINK -> {
                    feedMetadataBuilder.link = pullParser.nextText()
                }

                TAG_DESCRIPTION -> {
                    feedMetadataBuilder.description = pullParser.nextText()
                }

                TAG_LAST_BUILD_DATE -> {
                    feedMetadataBuilder.lastBuildDate = pullParser.nextText()
                }

                TAG_IMAGE,
                TAG_ITUNES_IMAGE -> {
                    feedMetadataBuilder.image = parseFeedImage(pullParser)
                }

                TAG_RSS_ITEM -> {
                    break
                }

                else -> {
                    // TODO: Log missed tags
                    pullParser.skipSubTree()
                }
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

    private fun parseItem(pullParser: XmlPullParser): ParsedFeedItem {
        return ParsedFeedItem.build {
            while (tagNotClosed(pullParser = pullParser, tag = TAG_RSS_ITEM)) {
                if (pullParser.eventType != EventType.START_TAG) continue

//                var content: String? = null
//                var image: String? = null
//                var audio: String? = null
//                var video: String? = null
//                var sourceName: String? = null
//                var sourceUrl: String? = null
//                var categories: List<String> = emptyList()
//                var commentsUrl: String? = null
//                var youtubeItemData: ParsedFeedItemYoutubeData? = null
//                var rawEnclosure: ParsedFeedItemRawEnclosure? = null
//                var rawMediaContent: ParsedFeedItemMediaContent? = null

                when (pullParser.name) {
                    TAG_GUID -> {
                        guid = pullParser.nextText()
                    }

                    TAG_TITLE -> {
                        title = pullParser.nextText()
                    }

                    TAG_LINK -> {
                        link = pullParser.nextText()
                    }

                    TAG_DESCRIPTION -> {
                        description = pullParser.nextText()
                    }

                    TAG_PUB_DATE -> {
                        pubDate = pullParser.nextText()
                    }

                    TAG_ENCLOSURE -> {
                        val url = pullParser.getAttributeValue(pullParser.namespace, ATTR_URL)
                        val type = pullParser.getAttributeValue(pullParser.namespace, ATTR_TYPE)

                        if (url.isNullOrBlank() || type.isNullOrBlank()) continue

                        when {
                            type.startsWith(ATTR_VAL_IMAGE_PREFIX) -> {
                                image = url
                            }

                            type.startsWith(ATTR_VAL_AUDIO_PREFIX) -> {
                                audio = url
                            }

                            type.startsWith(ATTR_VAL_VIDEO_PREFIX) -> {
                                video = url
                            }

                            link.isNullOrBlank() -> {
                                link = url
                            }
                        }
                    }

                    TAG_MEDIA_CONTENT if (image.isNullOrBlank()) -> {
                        val url = pullParser.getAttributeValue(pullParser.namespace, ATTR_URL)
                        val medium =
                            pullParser.getAttributeValue(pullParser.namespace, ATTR_MEDIUM)
                        if (medium == ATTR_VAL_IMAGE || url?.isNotBlank() == true) {
                            image = url
                        }
                    }

                    TAG_ITUNES_IMAGE if (image.isNullOrBlank()) -> {
                        image = pullParser.getAttributeValue(pullParser.namespace, ATTR_HREF)
                    }

                    TAG_CONTENT_ENCODED -> {
                        content = pullParser.nextText()
                    }

                    TAG_DC_CREATOR, TAG_AUTHOR -> {
                        author = pullParser.nextText()
                    }

                    TAG_CATEGORY -> {
                        categories = categories + pullParser.nextText()
                    }

                    else -> {
                        pullParser.skipSubTree()
                    }
                }
            }
        }
    }

    private fun parseFeedImage(pullParser: XmlPullParser): ParsedFeedImage? {
        var url: String? = null
        var title: String? = null
        var link: String? = null
        var description: String? = null

        if (pullParser.name == TAG_ITUNES_IMAGE) {
            url = pullParser.getAttributeValue(pullParser.namespace, ATTR_HREF)
            return ParsedFeedImage(
                title = title,
                url = url,
                link = link,
                description = description
            )
        }

        while (tagNotClosed(pullParser = pullParser, tag = TAG_IMAGE)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_URL -> url = pullParser.nextText()
                TAG_TITLE -> title = pullParser.nextText()
                TAG_LINK -> link = pullParser.nextText()
                TAG_DESCRIPTION -> description = pullParser.nextText()
                else -> pullParser.skipSubTree()
            }
        }

        if (url.isNullOrBlank()) return null

        return ParsedFeedImage(
            title = title,
            url = url,
            link = link,
            description = description
        )
    }

}