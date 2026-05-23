package dev.shounakmulay.devpulse.core.domain.feed

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateUrlUseCaseTest {
    private val useCase = ValidateUrlUseCase()

    @Test
    fun `Given https url When validating Then returns valid url`() {
        assertEquals(
            UrlValidationResult.Valid("https://example.com/feed.xml"),
            useCase("https://example.com/feed.xml")
        )
    }

    @Test
    fun `Given protocol relative url When validating Then returns https url`() {
        assertEquals(
            UrlValidationResult.Valid("https://example.com/rss"),
            useCase("//example.com/rss")
        )
    }

    @Test
    fun `Given empty url When validating Then returns empty error`() {
        assertEquals(
            UrlValidationResult.Invalid(UrlValidationError.EMPTY),
            useCase("  ")
        )
    }

    @Test
    fun `Given unsupported scheme When validating Then returns unsupported scheme error`() {
        assertEquals(
            UrlValidationResult.Invalid(UrlValidationError.UNSUPPORTED_SCHEME),
            useCase("ftp://example.com/feed.xml")
        )
    }

    @Test
    fun `Given url with whitespace When validating Then returns invalid characters error`() {
        assertEquals(
            UrlValidationResult.Invalid(UrlValidationError.INVALID_CHARACTERS),
            useCase("https://exa mple.com/feed.xml")
        )
    }

    @Test
    fun `Given url without host When validating Then returns missing host error`() {
        assertEquals(
            UrlValidationResult.Invalid(UrlValidationError.MISSING_HOST),
            useCase("https:///feed.xml")
        )
    }

    @Test
    fun `Given host without dot When validating Then returns invalid host error`() {
        assertEquals(
            UrlValidationResult.Invalid(UrlValidationError.INVALID_HOST),
            useCase("https://localhost/feed.xml")
        )
    }
}
