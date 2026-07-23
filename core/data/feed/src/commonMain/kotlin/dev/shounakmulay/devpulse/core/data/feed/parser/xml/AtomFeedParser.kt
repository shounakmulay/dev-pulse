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
class AtomFeedParser(
    private val articleHtmlParser: ArticleHtmlParser,
) : FeedParser() {

    override suspend fun parse(pullParser: XmlPullParser): ParsedFeed {
        val feedMetadataBuilder = ParsedFeedMetadata.Companion.Builder()

        while (tagNotClosed(pullParser = pullParser, tag = TAG_ATOM)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_TITLE -> {
                    feedMetadataBuilder.title = pullParser.nextText()
                }

                TAG_LINK -> {
                    if (feedMetadataBuilder.link.isNullOrBlank()) {
                        feedMetadataBuilder.link = parseAtomLinkHref(pullParser)
                    }
                }

                TAG_SUBTITLE -> {
                    feedMetadataBuilder.description = pullParser.nextText()
                }

                TAG_UPDATED -> {
                    feedMetadataBuilder.lastBuildDate = pullParser.nextText()
                }

                TAG_ICON, TAG_LOGO -> {
                    feedMetadataBuilder.image = ParsedFeedImage(
                        title = null,
                        url = pullParser.nextText(),
                        link = null,
                        description = null
                    )
                }

                TAG_ITUNES_IMAGE -> {
                    feedMetadataBuilder.image = ParsedFeedImage(
                        title = null,
                        url = pullParser.getAttributeValue(pullParser.namespace, ATTR_HREF),
                        link = null,
                        description = null
                    )
                }

                TAG_ATOM_ENTRY -> {
                    break
                }

                else -> {
                    pullParser.skipSubTree()
                }
            }
        }

        return ParsedFeed(
            metadata = feedMetadataBuilder.build(),
            items = feedItemsFlow(pullParser = pullParser, itemTag = TAG_ATOM_ENTRY) {
                parseEntry(pullParser)
            },
            issues = listOf()
        )
    }

    private fun parseEntry(pullParser: XmlPullParser): ParsedFeedItem {
        return ParsedFeedItem.build {
            while (tagNotClosed(pullParser = pullParser, tag = TAG_ATOM_ENTRY)) {
                if (pullParser.eventType != EventType.START_TAG) continue

                when (pullParser.name) {
                    TAG_ID -> {
                        guid = pullParser.nextText()
                    }

                    TAG_TITLE -> {
                        title = pullParser.nextText()
                    }

                    TAG_LINK -> {
                        val rel = pullParser.getAttributeValue(pullParser.namespace, ATTR_REL)
                        val href = pullParser.getAttributeValue(pullParser.namespace, ATTR_HREF)

                        when {
                            rel.isNullOrBlank() || rel == ATTR_VAL_ALTERNATE -> {
                                link = link ?: href
                            }

                            rel == ATTR_VAL_ENCLOSURE -> {
                                readAtomEnclosure(
                                    itemBuilder = this,
                                    href = href,
                                    type = pullParser.getAttributeValue(
                                        pullParser.namespace,
                                        ATTR_TYPE
                                    )
                                )
                            }
                        }
                    }

                    TAG_PUBLISHED -> {
                        pubDate = pullParser.nextText()
                    }

                    TAG_UPDATED -> {
                        val updated = pullParser.nextText()
                        pubDate = pubDate ?: updated
                    }

                    TAG_SUMMARY -> {
                        val rawSummary = pullParser.nextText()
                        description = rawSummary
                        // Extract hero image from summary HTML as fallback
                        if (image.isNullOrBlank()) {
                            image = extractHeroImage(rawSummary)
                        }
                    }

                    TAG_CONTENT -> {
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

                    TAG_AUTHOR -> {
                        author = parseAuthor(pullParser)
                    }

                    TAG_CATEGORY -> {
                        val term = pullParser.getAttributeValue(pullParser.namespace, ATTR_TERM)
                        if (!term.isNullOrBlank()) {
                            categories = categories + term
                        }
                    }

                    else -> {
                        pullParser.skipSubTree()
                    }
                }
            }
        }
    }

    private fun readAtomEnclosure(
        itemBuilder: ParsedFeedItem.Companion.Builder,
        href: String?,
        type: String?
    ) {
        if (href.isNullOrBlank()) return

        when {
            type?.startsWith(ATTR_VAL_IMAGE_PREFIX) == true -> {
                itemBuilder.image = itemBuilder.image ?: href
            }

            type?.startsWith(ATTR_VAL_AUDIO_PREFIX) == true -> {
                itemBuilder.audio = itemBuilder.audio ?: href
            }

            type?.startsWith(ATTR_VAL_VIDEO_PREFIX) == true -> {
                itemBuilder.video = itemBuilder.video ?: href
            }

            itemBuilder.link.isNullOrBlank() -> {
                itemBuilder.link = href
            }
        }
    }

    private fun parseAuthor(pullParser: XmlPullParser): String? {
        var author: String? = null

        while (tagNotClosed(pullParser = pullParser, tag = TAG_AUTHOR)) {
            if (pullParser.eventType != EventType.START_TAG) continue

            when (pullParser.name) {
                TAG_NAME -> {
                    val name = pullParser.nextText()
                    author = author ?: name
                }

                else -> pullParser.skipSubTree()
            }
        }

        return author
    }

    private fun parseAtomLinkHref(pullParser: XmlPullParser): String? {
        val rel = pullParser.getAttributeValue(pullParser.namespace, ATTR_REL)

        if (rel.isNullOrBlank() || rel == ATTR_VAL_ALTERNATE) {
            return pullParser.getAttributeValue(pullParser.namespace, ATTR_HREF)
        }

        return null
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
}