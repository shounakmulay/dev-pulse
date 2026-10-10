package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.ui.text.AnnotatedStringTextResource
import dev.shounakmulay.devpulse.core.ui.text.SimpleTextResource
import dev.shounakmulay.devpulse.core.ui.text.TextResourceSpan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeedSearchInteractorTest {
    private val interactor = FeedSearchInteractor(ExtractInitialsUseCase())

    @Test
    fun `Given named pinned search result When mapping Then feed keeps its display name and actions`() {
        val feed = interactor.toUIFeed(result(name = "My Kotlin", pinned = true))

        assertEquals("My Kotlin", feed.title)
        assertEquals("MK", feed.initials)
        assertEquals(true, feed.pinned)
        assertEquals(UUID("feed-id"), feed.id)
        assertEquals("https://example.com/rss", feed.sourceUrl)
        assertEquals(SimpleTextResource("My Kotlin"), feed.searchHighlights?.highlightedName)
        assertEquals(SimpleTextResource("A feed description"), feed.searchHighlights?.highlightedDescription)
    }

    @Test
    fun `Given unnamed search result When mapping Then title and its highlight remain visible`() {
        val feed = interactor.toUIFeed(result(name = "", pinned = false))

        assertEquals("Kotlin Weekly", feed.title)
        assertEquals("KW", feed.initials)
        assertNull(feed.searchHighlights?.highlightedName)
        assertEquals(SimpleTextResource("Kotlin Weekly"), feed.searchHighlights?.highlightedTitle)
    }

    @Test
    fun `Given marked search title When mapping Then visible title and bold range exclude markers`() {
        val feed = interactor.toUIFeed(
            result(name = "", pinned = false).copy(highlightedTitle = "\uE000Kotlin\uE001 Weekly")
        )

        assertEquals(
            AnnotatedStringTextResource(
                text = "Kotlin Weekly",
                spans = listOf(TextResourceSpan(start = 0, end = 6, fontWeight = 700)),
            ),
            feed.searchHighlights?.highlightedTitle,
        )
    }

    private fun result(name: String, pinned: Boolean) = RssFeedSearchResult(
        id = UUID("feed-id"),
        title = "Kotlin Weekly",
        name = name,
        image = null,
        link = "https://example.com",
        sourceUrl = "https://example.com/rss",
        pinned = pinned,
        highlightedTitle = "Kotlin Weekly",
        highlightedName = name,
        highlightedDescription = "A feed description",
    )
}
