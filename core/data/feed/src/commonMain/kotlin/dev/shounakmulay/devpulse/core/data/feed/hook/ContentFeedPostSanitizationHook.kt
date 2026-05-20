package dev.shounakmulay.devpulse.core.data.feed.hook

import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.feed.hook.model.PostWithIdentity
import org.koin.core.annotation.Factory

@Factory
internal class ContentFeedPostSanitizationHook : CoreItemHook<PostWithIdentity> {

    override suspend fun process(post: PostWithIdentity): PostWithIdentity {
        return post.copy(post = post.post.sanitized())
    }

    private fun LocalRssContentFeedPost.sanitized(): LocalRssContentFeedPost {
        return copy(
            guid = guid.sanitizePlainText(),
            title = title.sanitizePlainText(),
            author = author.sanitizePlainText(),
            link = link.sanitizeUrl(),
            description = description.sanitizeRichText(),
            content = content.sanitizeRichText(),
            image = image.sanitizeUrl(),
            audio = audio.sanitizeUrl(),
            video = video.sanitizeUrl(),
            sourceName = sourceName.sanitizePlainText(),
            sourceUrl = sourceUrl.sanitizeUrl(),
            categories = categories.sanitizeCategories(),
            commentsUrl = commentsUrl.sanitizeUrl(),
            youtubeData = youtubeData?.sanitized(),
            rawEnclosure = rawEnclosure?.sanitized(),
            rawMedia = rawMedia?.sanitized()
        )
    }

    private fun LocalRssFeedItemYoutubeData.sanitized(): LocalRssFeedItemYoutubeData {
        return copy(
            title = title.sanitizeRichText(),
            videoUrl = videoUrl.sanitizeUrl(),
            thumbnailUrl = thumbnailUrl.sanitizeUrl(),
            description = description.sanitizeRichText()
        )
    }

    private fun LocalRssFeedItemRawEnclosure.sanitized(): LocalRssFeedItemRawEnclosure {
        return copy(
            url = url.sanitizeUrl(),
            type = type.sanitizeMimeText()
        )
    }

    private fun LocalRssFeedItemMediaContent.sanitized(): LocalRssFeedItemMediaContent {
        return copy(
            url = url.sanitizeUrl(),
            type = type.sanitizeMimeText(),
            medium = medium.sanitizeMimeText()
        )
    }

    private fun String?.sanitizeRichText(): String? {
        return this
            ?.decodeBasicHtmlEntities()
            ?.removeScriptAndStyleBlocks()
            ?.stripHtmlTags()
            ?.collapseWhitespace()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun String?.sanitizePlainText(): String? {
        return this
            ?.decodeBasicHtmlEntities()
            ?.collapseWhitespace()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun String.sanitizeCategories(): String {
        return decodeBasicHtmlEntities()
            .collapseWhitespace()
    }

    private fun String?.sanitizeMimeText(): String? {
        return sanitizePlainText()?.lowercase()
    }

    private fun String?.sanitizeUrl(): String? {
        val value = sanitizePlainText() ?: return null
        val normalized = when {
            value.startsWith("//") -> "https:$value"
            else -> value
        }
        val scheme = normalized.substringBefore(":", missingDelimiterValue = "")
        if (scheme.lowercase() !in setOf("http", "https")) return null

        val schemeDelimiterIndex = normalized.indexOf("://")
        if (schemeDelimiterIndex < 0) return null

        val authorityStart = schemeDelimiterIndex + 3
        val authorityEnd = normalized.indexOfAny(charArrayOf('/', '?', '#'), authorityStart)
            .takeIf { it >= 0 } ?: normalized.length
        val authority = normalized.substring(authorityStart, authorityEnd)

        return normalized.takeIf {
            authority.isNotBlank() &&
                authority.contains('.') &&
                !authority.contains(Regex("\\s")) &&
                !it.contains(Regex("[<>]"))
        }
    }

    private fun String.removeScriptAndStyleBlocks(): String {
        return replace(scriptOrStyleRegex, " ")
    }

    private fun String.stripHtmlTags(): String {
        return replace(htmlTagRegex, " ")
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
            entity.startsWith("&#x", ignoreCase = true) -> {
                entity.removePrefix("&#x").removePrefix("&#X").removeSuffix(";").toIntOrNull(16)
            }
            entity.startsWith("&#") -> entity.removePrefix("&#").removeSuffix(";").toIntOrNull()
            else -> null
        } ?: return null

        return value.toChar().toString()
    }

    private fun String.collapseWhitespace(): String {
        return trim().replace(whitespaceRegex, " ")
    }

    private companion object {
        val scriptOrStyleRegex = Regex(
            pattern = "<(script|style)\\b[^>]*>[\\s\\S]*?</\\1>",
            options = setOf(RegexOption.IGNORE_CASE)
        )
        val htmlTagRegex = Regex("<[^>]+>")
        val entityRegex = Regex("&(?:amp|lt|gt|quot|apos|nbsp|#39|#[0-9]+|#x[0-9a-fA-F]+);")
        val whitespaceRegex = Regex("\\s+")
    }
}
