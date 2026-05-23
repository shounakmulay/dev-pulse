package dev.shounakmulay.devpulse.core.domain.feed

import org.koin.core.annotation.Factory

@Factory
class ValidateUrlUseCase {

    operator fun invoke(url: String): UrlValidationResult {
        val value = url.trim()
        if (value.isEmpty()) {
            return UrlValidationResult.Invalid(UrlValidationError.EMPTY)
        }

        val normalized = when {
            value.startsWith("http://") || value.startsWith("https://") -> value
            value.startsWith("//") -> "https:$value"
            !value.startsWith("http://") || !value.startsWith("https://") -> "https://$value"
            else -> value
        }

        val scheme = normalized.substringBefore(":", missingDelimiterValue = "").lowercase()
        if (scheme !in SupportedSchemes) {
            return UrlValidationResult.Invalid(UrlValidationError.UNSUPPORTED_SCHEME)
        }

        val schemeDelimiterIndex = normalized.indexOf("://")
        if (schemeDelimiterIndex < 0) {
            return UrlValidationResult.Invalid(UrlValidationError.MISSING_SCHEME_DELIMITER)
        }

        if (normalized.contains(invalidUrlCharactersRegex)) {
            return UrlValidationResult.Invalid(UrlValidationError.INVALID_CHARACTERS)
        }

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
            authority.isBlank() || host.isBlank() -> UrlValidationResult.Invalid(
                UrlValidationError.MISSING_HOST
            )

            !host.contains('.') -> UrlValidationResult.Invalid(UrlValidationError.INVALID_HOST)
            else -> UrlValidationResult.Valid(normalized)
        }
    }

    private companion object {
        const val SchemeDelimiter = "://"
        val SupportedSchemes = setOf("http", "https")
        val invalidUrlCharactersRegex = Regex("\\s|[<>]")
    }
}

sealed interface UrlValidationResult {
    data class Valid(val url: String) : UrlValidationResult
    data class Invalid(val error: UrlValidationError) : UrlValidationResult
}

enum class UrlValidationError {
    EMPTY,
    UNSUPPORTED_SCHEME,
    MISSING_SCHEME_DELIMITER,
    INVALID_CHARACTERS,
    MISSING_HOST,
    INVALID_HOST
}
