package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.data.feed.parser.common.ArticleHtmlParser
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedImage
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedMetadata
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.koin.core.annotation.Factory

@Factory
class RssFeedParser(
    private val articleHtmlParser: ArticleHtmlParser,
) : FeedParser() {

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
                        val rawDescription = pullParser.nextText()
                        description = rawDescription
                        // Try extracting hero image from description HTML if no image yet
                        if (image.isNullOrBlank()) {
                            image = extractHeroImage(rawDescription)
                        }
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
                        val rawContent = pullParser.nextText()
                        content = rawContent
                        // Extract hero image from content HTML as fallback
                        if (image.isNullOrBlank()) {
                            image = extractHeroImage(rawContent)
                        }
                        // Extract audio from content HTML as fallback
                        if (audio.isNullOrBlank()) {
                            audio = extractAudioUrl(rawContent)
                        }
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

    /**
     * Parses HTML content using [ArticleHtmlParser] to extract the first non-GIF
     * `<img src>` URL. Returns null if the HTML is blank or no suitable image is found.
     *
     * Ported from Twine's `XmlContentParser.parsePostContent()` → `ArticleHtmlParser.parse()`.
     */
    private fun extractHeroImage(htmlContent: String?): String? {
        if (htmlContent.isNullOrBlank()) return null
        return articleHtmlParser.parse(htmlContent)?.heroImage
    }

    /**
     * Parses HTML content using [ArticleHtmlParser] to extract the first
     * `<audio src>` URL. Returns null if the HTML is blank or no audio is found.
     *
     * Ported from Twine's `XmlContentParser.parsePostContent()` → `ArticleHtmlParser.parse()`.
     */
    private fun extractAudioUrl(htmlContent: String?): String? {
        if (htmlContent.isNullOrBlank()) return null
        return articleHtmlParser.parse(htmlContent)?.audioUrl
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