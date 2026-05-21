package dev.shounakmulay.devpulse.core.data.feed.mapper

import com.prof18.rssparser.model.RssChannel
import dev.shounakmulay.devpulse.core.common.time.DateTimeParser
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.RssIdentityGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RssFeedMapperTest {

    private val mapper = RssFeedMapper(
        identityGenerator = RssIdentityGenerator(),
        dateTimeParser = DateTimeParser()
    )

    @Test
    fun `Given RSS channel date When mapped to local feed Then date is epoch milliseconds`() {
        val result = mapper.toLocalRssFeed(
            from = createChannel(lastBuildDate = "Tue, 19 May 2026 10:00:00 GMT"),
            existingIdentity = LocalRssFeedIdentity(
                id = "feed-1",
                sourceUrl = "https://example.com/feed.xml",
                link = "https://example.com",
                createdAt = 1234L,
                updatedAt = 5678L
            ),
            url = "https://example.com/feed.xml"
        )

        assertEquals(1779184800000L, result.lastBuildDate)
        assertEquals(1234L, result.createdAt)
        assertTrue(result.updatedAt > 1_000_000_000_000L)
    }

    private fun createChannel(lastBuildDate: String?): RssChannel {
        return RssChannel(
            title = "Title",
            link = "https://example.com",
            description = "Description",
            image = null,
            lastBuildDate = lastBuildDate,
            updatePeriod = null,
            items = emptyList(),
            itunesChannelData = null,
            youtubeChannelData = null
        )
    }
}
