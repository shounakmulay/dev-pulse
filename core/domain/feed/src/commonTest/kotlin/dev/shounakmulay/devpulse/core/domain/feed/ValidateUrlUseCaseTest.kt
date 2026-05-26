package dev.shounakmulay.devpulse.core.domain.feed

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateUrlUseCaseTest {
    private val useCase = ValidateUrlUseCase()

    @Test
    fun `Given https url When validating Then returns true`() {
        assertEquals(true, useCase("https://example.com/feed.xml"))
    }

    @Test
    fun `Given host and path When validating Then returns true`() {
        assertEquals(true, useCase("example.com/rss"))
    }

    @Test
    fun `Given protocol relative url When validating Then returns true`() {
        assertEquals(true, useCase("//example.com/rss"))
    }

    @Test
    fun `Given empty url When validating Then returns false`() {
        assertEquals(false, useCase("  "))
    }

    @Test
    fun `Given unsupported scheme When validating Then returns false`() {
        assertEquals(false, useCase("ftp://example.com/feed.xml"))
    }

    @Test
    fun `Given url with whitespace When validating Then returns false`() {
        assertEquals(false, useCase("https://exa mple.com/feed.xml"))
    }

    @Test
    fun `Given url without host When validating Then returns false`() {
        assertEquals(false, useCase("https:///feed.xml"))
    }

    @Test
    fun `Given host without dot When validating Then returns false`() {
        assertEquals(false, useCase("https://localhost/feed.xml"))
    }
}
