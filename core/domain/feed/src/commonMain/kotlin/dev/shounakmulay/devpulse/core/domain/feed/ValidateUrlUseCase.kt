package dev.shounakmulay.devpulse.core.domain.feed

import org.koin.core.annotation.Factory

@Factory
class ValidateUrlUseCase {

    operator fun invoke(url: String): Boolean = UrlNormalizer.normalize(url) != null
}

@Factory
class NormalizeUrlUseCase {

    operator fun invoke(url: String): String? = UrlNormalizer.normalize(url)
}

private object UrlNormalizer {
    private const val SchemeDelimiter = "://"
    private val SupportedSchemes = setOf("http", "https")
    private val invalidUrlCharactersRegex = Regex("\\s|[<>]")

    fun normalize(url: String): String? {
        val value = url.trim()
        if (value.isEmpty() || value.contains(invalidUrlCharactersRegex)) return null

        val normalized = when {
            value.startsWith("http://") || value.startsWith("https://") -> value
            value.startsWith("//") -> "https:$value"
            SchemeDelimiter in value -> return null
            else -> "https://$value"
        }

        val scheme = normalized.substringBefore(":", missingDelimiterValue = "").lowercase()
        if (scheme !in SupportedSchemes) return null

        val schemeDelimiterIndex = normalized.indexOf(SchemeDelimiter)
        if (schemeDelimiterIndex < 0) return null

        val authorityStart = schemeDelimiterIndex + SchemeDelimiter.length
        val authorityEnd = normalized.indexOfAny(
            chars = charArrayOf('/', '?', '#'),
            startIndex = authorityStart
        ).takeIf { it >= 0 } ?: normalized.length
        val authority = normalized.substring(authorityStart, authorityEnd)
        val host = authority
            .substringAfterLast('@')
            .substringBefore(':')
            .trim()

        return when {
            authority.isBlank() || host.isBlank() -> null
            !host.contains('.') -> null
            else -> normalized
        }
    }
}
