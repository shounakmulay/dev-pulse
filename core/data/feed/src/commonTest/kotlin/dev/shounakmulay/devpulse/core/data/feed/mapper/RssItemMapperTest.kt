package dev.shounakmulay.devpulse.core.data.feed.mapper

import com.prof18.rssparser.model.RssItem
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.RssIdentityGenerator
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class RssItemMapperTest {

    private val mapper = RssItemMapper(
        idGenerator = RssIdentityGenerator(),
        dateTimeProvider = FixedDateTimeProvider
    )

    @Test
    fun `Given RSS item date When mapped to local post Then raw publisher date is preserved`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "Tue, 19 May 2026 10:00:00 +0000"),
            feedId = "feed-1",
            fingerprint = "fingerprint-1",
            existingIdentity = LocalRssContentFeedPostIdentity(
                id = "post-1",
                fingerprint = "fingerprint-1",
                createdAt = 1234L,
                updatedAt = 5678L
            )
        )

        assertEquals("Tue, 19 May 2026 10:00:00 +0000", result.pubDate)
        assertEquals(1234L, result.createdAt)
        assertEquals(1779184800000L, result.updatedAt)
    }

    private fun createItem(pubDate: String?): RssItem {
        return RssItem(
            guid = "guid-1",
            title = "Title",
            author = "Author",
            link = "https://example.com/post",
            pubDate = pubDate,
            description = "Description",
            content = "Content",
            image = null,
            audio = null,
            video = null,
            sourceName = "Source",
            sourceUrl = "https://example.com/feed.xml",
            categories = emptyList(),
            itunesItemData = null,
            commentsUrl = null,
            youtubeItemData = null,
            rawEnclosure = null,
            rawMediaContent = null
        )
    }

    private object FixedDateTimeProvider : DateTimeProvider {
        override fun now(): Long = nowEpochMilliseconds()

        override fun nowEpochMilliseconds(): Long = 1779184800000L

        override fun today(): LocalDate = LocalDate(year = 2026, monthNumber = 5, dayOfMonth = 19)
    }
}
