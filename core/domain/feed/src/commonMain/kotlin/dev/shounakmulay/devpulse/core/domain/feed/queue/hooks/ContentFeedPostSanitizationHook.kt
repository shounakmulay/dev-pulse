package dev.shounakmulay.devpulse.core.domain.feed.queue.hooks

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostRawEnclosure
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostYoutubeData
import org.koin.core.annotation.Factory

@Factory
class ContentFeedPostSanitizationHook : CorePostsItemHook {

    override suspend fun process(
        actionType: RssFeedQueueActionType,
        post: RssFeedPostWithExistingIdentity
    ): RssFeedPostWithExistingIdentity {
        return post.copy(post = post.post.sanitized())
    }

    private fun RssFeedPost.sanitized(): RssFeedPost {
        return copy(
            guid = guid.sanitizePlainText(),
            title = title.sanitizePlainText().orEmpty(),
            author = author.sanitizePlainText().orEmpty(),
            link = link.sanitizeUrl(),
            description = description.sanitizeRichText(),
            content = content,
            image = image.sanitizeUrl(),
            audio = audio.sanitizeUrl(),
            video = video.sanitizeUrl(),
            sourceName = sourceName.sanitizePlainText().orEmpty(),
            sourceUrl = sourceUrl.sanitizeUrl().orEmpty(),
            categories = categories.map { it.sanitizeCategories() },
            commentsUrl = commentsUrl.sanitizeUrl(),
            youtubeItemData = youtubeItemData?.sanitized(),
            rawEnclosure = rawEnclosure?.sanitized(),
        )
    }

    private fun RssFeedPostYoutubeData.sanitized(): RssFeedPostYoutubeData {
        return copy(
            title = title.sanitizeRichText(),
            videoUrl = videoUrl.sanitizeUrl(),
            thumbnailUrl = thumbnailUrl.sanitizeUrl(),
            description = description.sanitizeRichText()
        )
    }

    private fun RssFeedPostRawEnclosure.sanitized(): RssFeedPostRawEnclosure {
        return copy(
            url = url.sanitizeUrl(),
            type = type.sanitizeMimeText()
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
