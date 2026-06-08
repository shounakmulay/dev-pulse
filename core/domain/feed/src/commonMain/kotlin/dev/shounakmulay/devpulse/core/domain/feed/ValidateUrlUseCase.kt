package dev.shounakmulay.devpulse.core.domain.feed

import org.koin.core.annotation.Factory

@Factory
class ValidateUrlUseCase {
    private companion object {
        const val SchemeDelimiter = "://"
        const val SupportedScheme = "https"

        val invalidUrlCharactersRegex = Regex("\\s|[<>]")
    }

    operator fun invoke(url: String): Boolean {
        if (url.isBlank() || url.contains(invalidUrlCharactersRegex)) return false

        val scheme = url.substringBefore(":", missingDelimiterValue = "").lowercase()
        if (scheme != SupportedScheme) return false

        val schemeDelimiterIndex = url.indexOf(SchemeDelimiter)
        if (schemeDelimiterIndex < 0) return false

        val authorityStart = schemeDelimiterIndex + SchemeDelimiter.length
        val authorityEnd = url.indexOfAny(
            chars = charArrayOf('/', '?', '#'),
            startIndex = authorityStart
        ).takeIf { it >= 0 } ?: url.length
        val authority = url.substring(authorityStart, authorityEnd)
        val host = authority
            .substringAfterLast('@')
            .substringBefore(':')
            .trim()

        return when {
            authority.isBlank() || host.isBlank() -> false
            !host.contains('.') -> false
            else -> true
        }
    }
}

