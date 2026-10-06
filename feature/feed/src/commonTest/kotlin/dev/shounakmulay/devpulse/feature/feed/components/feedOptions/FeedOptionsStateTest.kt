package dev.shounakmulay.devpulse.feature.feed.components.feedOptions

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeedOptionsStateTest {
    @Test
    fun `Given two feeds When opening the second Then its options belong to the second feed`() {
        val first = feed(id = "first", title = "First feed", pinned = false)
        val second = feed(id = "second", title = "Second feed", pinned = true)
        first.toFeedOptionsTarget()

        val target = second.toFeedOptionsTarget()

        assertEquals(UUID("second"), target.feedId)
        assertEquals(
            listOf(
                FeedOptionsMenuItem.Pin(true),
                FeedOptionsMenuItem.Share("Second feed", "https://second.example/feed"),
                FeedOptionsMenuItem.Delete("Second feed"),
            ),
            target.items,
        )
    }

    @Test
    fun `Given the second menu is open When the first anchor dismisses Then the second menu stays open`() {
        val open = FeedOptionsState.Open(feed("second", "Second feed", true).toFeedOptionsTarget())

        assertEquals(open, open.dismissMenu(UUID("first")))
        assertNull(open.dismissMenu(UUID("second")))
    }

    @Test
    fun `Given delete confirmation When its menu anchor disappears Then confirmation keeps its feed`() {
        val confirmation = FeedOptionsState.ConfirmingDelete(
            feedId = UUID("second"),
            option = FeedOptionsMenuItem.Delete("Second feed"),
        )

        assertEquals(confirmation, confirmation.dismissMenu(UUID("second")))
    }

    private fun feed(id: String, title: String, pinned: Boolean) = UIFeed(
        id = UUID(id),
        imageUrl = null,
        title = title,
        initials = title.take(1),
        pinned = pinned,
        sourceUrl = "https://$id.example/feed",
        websiteImageUrl = null,
    )
}
