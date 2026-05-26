package dev.shounakmulay.devpulse.core.domain.feed

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NormalizeUrlUseCaseTest {
    private val useCase = NormalizeUrlUseCase()

    @Test
    fun `Given https url When normalizing Then returns same url`() {
        assertEquals(
            "https://example.com/feed.xml",
            useCase("https://example.com/feed.xml")
        )
    }

    @Test
    fun `Given host and path When normalizing Then returns https url`() {
        assertEquals(
            "https://example.com/rss",
            useCase("example.com/rss")
        )
    }

    @Test
    fun `Given protocol relative url When normalizing Then returns https url`() {
        assertEquals(
            "https://example.com/rss",
            useCase("//example.com/rss")
        )
    }

    @Test
    fun `Given empty url When normalizing Then returns null`() {
        assertNull(useCase("  "))
    }

    @Test
    fun `Given unsupported scheme When normalizing Then returns null`() {
        assertNull(useCase("ftp://example.com/feed.xml"))
    }

    @Test
    fun `Given url with whitespace When normalizing Then returns null`() {
        assertNull(useCase("https://exa mple.com/feed.xml"))
    }

    @Test
    fun `Given url without host When normalizing Then returns null`() {
        assertNull(useCase("https:///feed.xml"))
    }

    @Test
    fun `Given host without dot When normalizing Then returns null`() {
        assertNull(useCase("https://localhost/feed.xml"))
    }
}
