package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedImage
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedIssue
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedMetadata
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.XmlPullReader
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.sanitized
import kotlinx.coroutines.flow.asFlow
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.kobjects.ktxml.mini.MiniXmlPullParser
import org.koin.core.annotation.Factory

@Factory
internal class KtXmlFeedParser {
    fun parseText(sourceUrl: String?, xml: String): ParsedFeed {
        return parse(sourceUrl = sourceUrl, chars = xml.iterator())
    }

    fun parse(sourceUrl: String?, chars: CharIterator): ParsedFeed {
        val issues = mutableListOf<ParsedFeedIssue>()
        val parser = MiniXmlPullParser(chars)
        val reader = XmlPullReader(sourceUrl = sourceUrl, issues = issues)
        val items = mutableListOf<ParsedFeedItem>()
        var metadata = emptyMetadata()
        var rootFound = false
        runCatching {
            while (parser.next() != EventType.END_DOCUMENT) {
                if (parser.eventType == EventType.START_TAG) {
                    rootFound = true
                    metadata = when {
                        reader.matches(parser, "rss") -> readRss(parser, reader, items)
                        reader.matches(parser, "channel") -> readChannel(parser, reader, items)
                        reader.matches(parser, "feed") -> readAtomFeed(parser, reader, items)
                        reader.matches(parser, "rdf:RDF", "RDF") -> readRdf(parser, reader, items)
                        else -> {
                            reader.skipCurrent(parser)
                            emptyMetadata()
                        }
                    }
                }
            }
        }.onFailure { throwable ->
            issues += ParsedFeedIssue(
                sourceUrl = sourceUrl,
                itemOrdinal = null,
                tag = null,
                attribute = null,
                message = throwable.message ?: "Unable to parse feed XML"
            )
        }
        if (!rootFound) {
            issues += ParsedFeedIssue(
                sourceUrl = sourceUrl,
                itemOrdinal = null,
                tag = null,
                attribute = null,
                message = "No supported feed root found"
            )
        }
        val sanitizedItems = items.map { it.sanitized() }.filter { it.hasUsableData() }
        return ParsedFeed(
            metadata = metadata.sanitized(),
            items = sanitizedItems.asFlow(),
            itemCount = sanitizedItems.size,
            issues = issues
        )
    }

    private fun readRss(
        parser: XmlPullParser,
        reader: XmlPullReader,
        items: MutableList<ParsedFeedItem>
    ): ParsedFeedMetadata {
        var metadata = emptyMetadata()
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "channel") -> metadata = readChannel(parser, reader, items)
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "rss")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return metadata
    }

    private fun readChannel(
        parser: XmlPullParser,
        reader: XmlPullReader,
        items: MutableList<ParsedFeedItem>
    ): ParsedFeedMetadata {
        val builder = FeedMetadataBuilder()
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "item") -> items += readItem(parser, reader, items.size)
                    reader.matches(parser, "image") -> builder.image = readFeedImage(parser, reader, builder)
                    reader.matches(parser, "title", "dc:title", "itunes:title") -> {
                        builder.title = builder.title ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "link") -> builder.link = builder.link ?: reader.readElementText(parser)
                    reader.matches(parser, "atom:link") -> readAtomLink(parser, reader, builder)
                    reader.matches(parser, "description", "dc:description", "itunes:summary", "itunes:subtitle") -> {
                        builder.description = builder.description ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "lastBuildDate", "pubDate", "dc:date") -> {
                        builder.lastBuildDate = builder.lastBuildDate ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "sy:updatePeriod", "ttl") -> {
                        builder.updatePeriod = builder.updatePeriod ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "yt:channelId", "youtube:channelId") -> {
                        builder.youtubeChannelId = builder.youtubeChannelId ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "itunes:image", "media:thumbnail", "media:content") -> {
                        readFeedImageCandidate(parser, reader, builder)
                    }
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "channel")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return builder.build()
    }

    private fun readAtomFeed(
        parser: XmlPullParser,
        reader: XmlPullReader,
        items: MutableList<ParsedFeedItem>
    ): ParsedFeedMetadata {
        val builder = FeedMetadataBuilder()
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "entry") -> items += readItem(parser, reader, items.size)
                    reader.matches(parser, "title") -> builder.title = builder.title ?: reader.readElementText(parser)
                    reader.matches(parser, "subtitle", "tagline") -> {
                        builder.description = builder.description ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "updated", "published") -> {
                        builder.lastBuildDate = builder.lastBuildDate ?: reader.readElementText(parser)
                    }
                    reader.matches(parser, "link") -> readAtomLink(parser, reader, builder)
                    reader.matches(parser, "icon", "logo") -> {
                        builder.image = builder.image ?: ParsedFeedImage(
                            title = builder.title,
                            url = reader.readElementText(parser),
                            link = builder.link,
                            description = builder.description
                        )
                    }
                    reader.matches(parser, "yt:channelId", "youtube:channelId") -> {
                        builder.youtubeChannelId = builder.youtubeChannelId ?: reader.readElementText(parser)
                    }
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "feed")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return builder.build()
    }

    private fun readRdf(
        parser: XmlPullParser,
        reader: XmlPullReader,
        items: MutableList<ParsedFeedItem>
    ): ParsedFeedMetadata {
        var metadata = emptyMetadata()
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "channel") -> metadata = readChannel(parser, reader, items)
                    reader.matches(parser, "item") -> items += readItem(parser, reader, items.size)
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "rdf:RDF", "RDF")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return metadata
    }

    private fun readFeedImage(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedMetadataBuilder
    ): ParsedFeedImage {
        val imageBuilder = FeedImageBuilder(title = builder.title, link = builder.link, description = builder.description)
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "title") -> imageBuilder.title = imageBuilder.title ?: reader.readElementText(parser)
                    reader.matches(parser, "url") -> imageBuilder.url = imageBuilder.url ?: reader.readElementText(parser)
                    reader.matches(parser, "link") -> imageBuilder.link = imageBuilder.link ?: reader.readElementText(parser)
                    reader.matches(parser, "description") -> {
                        imageBuilder.description = imageBuilder.description ?: reader.readElementText(parser)
                    }
                    else -> reader.skipCurrent(parser)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "image")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return imageBuilder.build()
    }

    private fun readFeedImageCandidate(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedMetadataBuilder
    ) {
        val url = reader.attribute(parser, "href", "url")
        if (url != null && builder.image == null) {
            builder.image = ParsedFeedImage(
                title = builder.title,
                url = url,
                link = builder.link,
                description = builder.description
            )
        }
        reader.skipCurrent(parser)
    }

    private fun readAtomLink(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedMetadataBuilder
    ) {
        val href = reader.attribute(parser, "href")
        val rel = reader.attribute(parser, "rel")
        if (href != null && builder.link == null && (rel == null || rel == "alternate" || rel == "self")) {
            builder.link = href
        }
        reader.skipCurrent(parser)
    }

    private fun readItem(
        parser: XmlPullParser,
        reader: XmlPullReader,
        ordinal: Int
    ): ParsedFeedItem {
        val builder = FeedItemBuilder(ordinal = ordinal)
        builder.guid = reader.attribute(parser, "rdf:about")
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "guid", "id", "dc:identifier") -> {
                        builder.guid = builder.guid ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "title", "dc:title", "media:title", "itunes:title") -> {
                        builder.title = builder.title ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "author", "dc:creator", "creator", "itunes:author") -> {
                        builder.author = builder.author ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "name") -> builder.author = builder.author ?: reader.readElementText(parser, ordinal)
                    reader.matches(parser, "link") -> readItemLink(parser, reader, builder)
                    reader.matches(parser, "pubDate", "published", "updated", "dc:date", "date") -> {
                        builder.pubDate = builder.pubDate ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(
                        parser,
                        "description",
                        "summary",
                        "dc:description",
                        "itunes:summary",
                        "itunes:subtitle",
                        "media:description"
                    ) -> {
                        builder.description = builder.description ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "content:encoded", "content") -> {
                        builder.content = builder.content ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "category", "dc:subject", "media:category", "itunes:category") -> {
                        readCategory(parser, reader, builder, ordinal)
                    }
                    reader.matches(parser, "media:keywords") -> readKeywords(parser, reader, builder, ordinal)
                    reader.matches(parser, "comments") -> {
                        builder.commentsUrl = builder.commentsUrl ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "source") -> readSource(parser, reader, builder, ordinal)
                    reader.matches(parser, "enclosure") -> readEnclosure(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:group") -> readMediaGroup(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:content") -> readMediaContent(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:thumbnail", "itunes:image") -> readItemImageCandidate(parser, reader, builder, ordinal)
                    reader.matches(parser, "yt:videoId", "youtube:videoId") -> {
                        builder.youtubeVideoId = builder.youtubeVideoId ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "media:community") -> readMediaCommunity(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:statistics", "statistics") -> readStatistics(parser, reader, builder)
                    reader.matches(parser, "media:starRating", "starRating") -> readStarRating(parser, reader, builder)
                    else -> reader.skipCurrent(parser, ordinal)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "item", "entry")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
        return builder.build()
    }

    private fun readMediaGroup(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "media:title") -> builder.title = builder.title ?: reader.readElementText(parser, ordinal)
                    reader.matches(parser, "media:description") -> {
                        builder.description = builder.description ?: reader.readElementText(parser, ordinal)
                    }
                    reader.matches(parser, "media:thumbnail") -> readItemImageCandidate(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:content") -> readMediaContent(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:community") -> readMediaCommunity(parser, reader, builder, ordinal)
                    reader.matches(parser, "media:statistics", "statistics") -> readStatistics(parser, reader, builder)
                    reader.matches(parser, "media:starRating", "starRating") -> readStarRating(parser, reader, builder)
                    else -> reader.skipCurrent(parser, ordinal)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "media:group")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
    }

    private fun readMediaCommunity(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        val startDepth = parser.depth
        var finished = false
        while (!finished) {
            when (parser.next()) {
                EventType.START_TAG -> when {
                    reader.matches(parser, "media:statistics", "statistics") -> readStatistics(parser, reader, builder)
                    reader.matches(parser, "media:starRating", "starRating") -> readStarRating(parser, reader, builder)
                    else -> reader.skipCurrent(parser, ordinal)
                }
                EventType.END_TAG -> finished = parser.depth == startDepth && reader.matches(parser, "media:community")
                EventType.END_DOCUMENT -> finished = true
                else -> Unit
            }
        }
    }

    private fun readItemLink(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder
    ) {
        val href = reader.attribute(parser, "href")
        val rel = reader.attribute(parser, "rel")
        val type = reader.attribute(parser, "type")
        val length = reader.attribute(parser, "length")?.toLongOrNull()
        if (href != null) {
            when (rel) {
                "enclosure" -> {
                    builder.rawEnclosure = builder.rawEnclosure ?: ParsedFeedItemRawEnclosure(
                        url = href,
                        length = length,
                        type = type
                    )
                    builder.setPrimaryMedia(url = href, type = type, medium = null)
                }
                null, "alternate" -> builder.link = builder.link ?: href
                else -> Unit
            }
            reader.skipCurrent(parser, builder.ordinal)
        } else {
            builder.link = builder.link ?: reader.readElementText(parser, builder.ordinal)
        }
    }

    private fun readCategory(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        val attributeCategory = reader.attribute(parser, "term", "label", "text")
        val textCategory = reader.readElementText(parser, ordinal)
        listOf(attributeCategory, textCategory)
            .filterNotNull()
            .forEach { builder.categories += it }
    }

    private fun readKeywords(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        reader.readElementText(parser, ordinal)
            ?.split(',')
            ?.forEach { builder.categories += it }
    }

    private fun readSource(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        builder.sourceUrl = builder.sourceUrl ?: reader.attribute(parser, "url")
        builder.sourceName = builder.sourceName ?: reader.readElementText(parser, ordinal)
    }

    private fun readEnclosure(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        val url = reader.attribute(parser, "url")
        val type = reader.attribute(parser, "type")
        builder.rawEnclosure = builder.rawEnclosure ?: ParsedFeedItemRawEnclosure(
            url = url,
            length = reader.attribute(parser, "length")?.toLongOrNull(),
            type = type
        )
        builder.setPrimaryMedia(url = url, type = type, medium = null)
        reader.skipCurrent(parser, ordinal)
    }

    private fun readMediaContent(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        val url = reader.attribute(parser, "url")
        val type = reader.attribute(parser, "type")
        val medium = reader.attribute(parser, "medium")
        builder.rawMediaContent = builder.rawMediaContent ?: ParsedFeedItemMediaContent(
            url = url,
            type = type,
            medium = medium
        )
        builder.setPrimaryMedia(url = url, type = type, medium = medium)
        reader.skipCurrent(parser, ordinal)
    }

    private fun readItemImageCandidate(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder,
        ordinal: Int
    ) {
        builder.image = builder.image ?: reader.attribute(parser, "href", "url")
        reader.skipCurrent(parser, ordinal)
    }

    private fun readStatistics(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder
    ) {
        builder.youtubeViewsCount = builder.youtubeViewsCount ?: reader.attribute(parser, "views")?.toIntOrNull()
        reader.skipCurrent(parser, builder.ordinal)
    }

    private fun readStarRating(
        parser: XmlPullParser,
        reader: XmlPullReader,
        builder: FeedItemBuilder
    ) {
        builder.youtubeLikesCount = builder.youtubeLikesCount ?: reader.attribute(parser, "count")?.toIntOrNull()
        reader.skipCurrent(parser, builder.ordinal)
    }

    private fun emptyMetadata(): ParsedFeedMetadata {
        return ParsedFeedMetadata(
            title = null,
            link = null,
            description = null,
            image = null,
            lastBuildDate = null,
            updatePeriod = null,
            youtubeChannel = null
        )
    }

    private fun ParsedFeedItem.hasUsableData(): Boolean {
        return listOf(guid, link, title, pubDate, description, content, image, audio, video)
            .any { !it.isNullOrBlank() }
    }

    private class FeedMetadataBuilder {
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var image: ParsedFeedImage? = null
        var lastBuildDate: String? = null
        var updatePeriod: String? = null
        var youtubeChannelId: String? = null

        fun build(): ParsedFeedMetadata {
            return ParsedFeedMetadata(
                title = title,
                link = link,
                description = description,
                image = image,
                lastBuildDate = lastBuildDate,
                updatePeriod = updatePeriod,
                youtubeChannel = youtubeChannelId?.let { ParsedFeedYoutubeChannel(channelId = it) }
            )
        }
    }

    private class FeedImageBuilder(
        var title: String?,
        var link: String?,
        var description: String?
    ) {
        var url: String? = null

        fun build(): ParsedFeedImage {
            return ParsedFeedImage(
                title = title,
                url = url,
                link = link,
                description = description
            )
        }
    }

    private class FeedItemBuilder(
        val ordinal: Int
    ) {
        var guid: String? = null
        var title: String? = null
        var author: String? = null
        var link: String? = null
        var pubDate: String? = null
        var description: String? = null
        var content: String? = null
        var image: String? = null
        var audio: String? = null
        var video: String? = null
        var sourceName: String? = null
        var sourceUrl: String? = null
        val categories = mutableListOf<String>()
        var commentsUrl: String? = null
        var youtubeVideoId: String? = null
        var youtubeViewsCount: Int? = null
        var youtubeLikesCount: Int? = null
        var rawEnclosure: ParsedFeedItemRawEnclosure? = null
        var rawMediaContent: ParsedFeedItemMediaContent? = null

        fun setPrimaryMedia(url: String?, type: String?, medium: String?) {
            if (url == null) return
            val normalizedMedium = medium?.lowercase()
            val normalizedType = type?.lowercase()
            when {
                normalizedMedium == "image" || normalizedType?.startsWith("image/") == true -> image = image ?: url
                normalizedMedium == "audio" || normalizedType?.startsWith("audio/") == true -> audio = audio ?: url
                normalizedMedium == "video" || normalizedType?.startsWith("video/") == true -> video = video ?: url
            }
        }

        fun build(): ParsedFeedItem {
            val videoId = youtubeVideoId ?: link?.extractYoutubeVideoId() ?: guid?.removePrefix("yt:video:")
            val videoUrl = link ?: videoId?.let { "https://www.youtube.com/watch?v=$it" }
            return ParsedFeedItem(
                ordinal = ordinal,
                guid = guid,
                title = title,
                author = author,
                link = link,
                pubDate = pubDate,
                description = description,
                content = content,
                image = image,
                audio = audio,
                video = video,
                sourceName = sourceName,
                sourceUrl = sourceUrl,
                categories = categories,
                commentsUrl = commentsUrl,
                youtubeItemData = videoId?.let {
                    ParsedFeedItemYoutubeData(
                        videoId = it,
                        title = title,
                        videoUrl = videoUrl,
                        thumbnailUrl = image,
                        description = description ?: content,
                        viewsCount = youtubeViewsCount,
                        likesCount = youtubeLikesCount
                    )
                },
                rawEnclosure = rawEnclosure,
                rawMediaContent = rawMediaContent
            )
        }
    }
}

private fun String.extractYoutubeVideoId(): String? {
    val watchId = substringAfter("v=", missingDelimiterValue = "")
        .substringBefore('&')
        .takeIf { it.isNotBlank() }
    if (watchId != null) return watchId
    return listOf("/shorts/", "/embed/").firstNotNullOfOrNull { marker ->
        substringAfter(marker, missingDelimiterValue = "")
            .substringBefore('?')
            .substringBefore('/')
            .takeIf { it.isNotBlank() }
    }
}
