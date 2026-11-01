package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArticleOriginTest {
    @Test
    fun `Given an article URL When extracting its origin Then path and parameters are omitted`() {
        assertEquals("https://example.com", articleOrigin("https://example.com/articles/private?token=secret#section"))
        assertEquals("http://example.com:8080", articleOrigin("HTTP://EXAMPLE.COM:8080/article"))
    }

    @Test
    fun `Given unsafe or oversized origins When extracting Then no origin is returned`() {
        assertNull(articleOrigin("https://user:password@example.com/article"))
        assertNull(articleOrigin("javascript://example.com/article"))
        assertNull(articleOrigin("https://${"a".repeat(100)}.example/article"))
    }
}
