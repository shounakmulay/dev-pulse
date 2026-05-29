package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedImage
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedMetadata
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.data.feed.parser.opml.model.ParsedOpmlFeed

internal fun ParsedFeedMetadata.sanitized(): ParsedFeedMetadata {
    return copy(
        title = title.sanitizePlainText(),
        link = link.sanitizeUrl(),
        description = description.sanitizeRichText(),
        image = image?.sanitized(),
        lastBuildDate = lastBuildDate.sanitizePlainText(),
        updatePeriod = updatePeriod.sanitizePlainText(),
        youtubeChannel = youtubeChannel?.sanitized()
    )
}

internal fun ParsedFeedItem.sanitized(): ParsedFeedItem {
    return copy(
        guid = guid.sanitizePlainText(),
        title = title.sanitizePlainText(),
        author = author.sanitizePlainText(),
        link = link.sanitizeUrl(),
        pubDate = pubDate.sanitizePlainText(),
        description = description.sanitizeRichText(),
        content = content.sanitizeRichText(),
        image = image.sanitizeUrl(),
        audio = audio.sanitizeUrl(),
        video = video.sanitizeUrl(),
        sourceName = sourceName.sanitizePlainText(),
        sourceUrl = sourceUrl.sanitizeUrl(),
        categories = categories.mapNotNull { it.sanitizePlainText() }.distinct(),
        commentsUrl = commentsUrl.sanitizeUrl(),
        youtubeItemData = youtubeItemData?.sanitized(),
        rawEnclosure = rawEnclosure?.sanitized(),
        rawMediaContent = rawMediaContent?.sanitized()
    )
}

internal fun ParsedOpmlFeed.sanitized(): ParsedOpmlFeed? {
    val url = xmlUrl.sanitizeUrl() ?: return null
    return copy(
        title = title.sanitizePlainText(),
        text = text.sanitizePlainText(),
        type = type.sanitizePlainText(),
        xmlUrl = url,
        htmlUrl = htmlUrl.sanitizeUrl(),
        description = description.sanitizeRichText(),
        categoryPath = categoryPath.mapNotNull { it.sanitizePlainText() }
    )
}

internal fun String?.sanitizePlainText(): String? {
    return this
        ?.removeUnsafeStorageCharacters()
        ?.decodeBasicHtmlEntities()
        ?.collapseWhitespace()
        ?.takeIf { it.isNotEmpty() }
}

internal fun String?.sanitizeRichText(): String? {
    return this
        ?.removeUnsafeStorageCharacters()
        ?.decodeBasicHtmlEntities()
        ?.collapseWhitespace()
        ?.takeIf { it.isNotEmpty() }
}

internal fun String?.sanitizeUrl(): String? {
    val value = sanitizePlainText() ?: return null
    val normalized = when {
        value.startsWith("//") -> "https:$value"
        else -> value
    }
    val scheme = normalized.substringBefore(":", missingDelimiterValue = "")
    if (scheme.lowercase() !in setOf("http", "https")) return null
    val delimiterIndex = normalized.indexOf("://")
    if (delimiterIndex < 0) return null
    val authorityStart = delimiterIndex + 3
    val authorityEnd = normalized.indexOfAny(charArrayOf('/', '?', '#'), authorityStart)
        .takeIf { it >= 0 } ?: normalized.length
    val authority = normalized.substring(authorityStart, authorityEnd)
    return normalized.takeIf {
        authority.isNotBlank() &&
            authority.contains('.') &&
            !authority.contains(Regex("\\s")) &&
            !it.contains(Regex("[<>\\u0000]"))
    }
}

private fun ParsedFeedImage.sanitized(): ParsedFeedImage {
    return copy(
        title = title.sanitizePlainText(),
        url = url.sanitizeUrl(),
        link = link.sanitizeUrl(),
        description = description.sanitizeRichText()
    )
}

private fun ParsedFeedYoutubeChannel.sanitized(): ParsedFeedYoutubeChannel {
    return copy(channelId = channelId.sanitizePlainText())
}

private fun ParsedFeedItemYoutubeData.sanitized(): ParsedFeedItemYoutubeData {
    return copy(
        videoId = videoId.sanitizePlainText(),
        title = title.sanitizePlainText(),
        videoUrl = videoUrl.sanitizeUrl(),
        thumbnailUrl = thumbnailUrl.sanitizeUrl(),
        description = description.sanitizeRichText()
    )
}

private fun ParsedFeedItemRawEnclosure.sanitized(): ParsedFeedItemRawEnclosure {
    return copy(
        url = url.sanitizeUrl(),
        type = type.sanitizePlainText()?.lowercase()
    )
}

private fun ParsedFeedItemMediaContent.sanitized(): ParsedFeedItemMediaContent {
    return copy(
        url = url.sanitizeUrl(),
        type = type.sanitizePlainText()?.lowercase(),
        medium = medium.sanitizePlainText()?.lowercase()
    )
}

private fun String.removeUnsafeStorageCharacters(): String {
    return filter { character ->
        character == '\n' ||
            character == '\r' ||
            character == '\t' ||
            character >= ' '
    }
}

private fun String.collapseWhitespace(): String {
    return replace(Regex("\\s+"), " ").trim()
}

private fun String.decodeBasicHtmlEntities(): String {
    return replace(entityRegex) { result ->
        when (val entity = result.value) {
            "&amp;" -> "&"
            "&lt;" -> "<"
            "&gt;" -> ">"
            "&quot;" -> "\""
            "&#39;", "&apos;" -> "'"
            "&nbsp;" -> " "
            else -> decodeNumericEntity(entity) ?: entity
        }
    }
}

private fun decodeNumericEntity(entity: String): String? {
    val value = when {
        entity.startsWith("&#x", ignoreCase = true) -> entity.drop(3).dropLast(1).toIntOrNull(16)
        entity.startsWith("&#") -> entity.drop(2).dropLast(1).toIntOrNull()
        else -> null
    } ?: return null
    return value.toChar().toString()
}

private val entityRegex = Regex("&(#x[0-9a-fA-F]+|#[0-9]+|amp|lt|gt|quot|apos|nbsp);")
