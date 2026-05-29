package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.RssIdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class RssPostMapperTest {
    private val mapper = RssPostMapper(
        idGenerator = RssIdentityGenerator(),
        dateTimeProvider = FixedDateTimeProvider
    )
    @Test
    fun `Given RSS item date When mapped to local post Then raw publisher date is preserved`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "Tue, 19 May 2026 10:00:00 +0000"),
            feedId = "feed-1",
            fingerprint = "fingerprint-1",
            existingIdentity = LocalRssContentFeedPostIdentitySlice(
                id = "post-1",
                fingerprint = "fingerprint-1",
                createdAt = 1234L,
                updatedAt = 5678L
            )
        )
        assertEquals("Tue, 19 May 2026 10:00:00 +0000", result.pubDate)
        assertEquals(1779184800000L, result.publishedAtEpochMillis)
        assertEquals(1234L, result.createdAt)
        assertEquals(1779184800000L, result.updatedAt)
    }
    @Test
    fun `Given RSS item GMT date When mapped to local post Then published time is parsed`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "Tue, 19 May 2026 10:00:00 GMT"),
            feedId = "feed-1",
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )
        assertEquals(1779184800000L, result.publishedAtEpochMillis)
    }
    @Test
    fun `Given RSS item offset date When mapped to local post Then published time is normalized to UTC`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "Tue, 19 May 2026 15:30:00 +0530"),
            feedId = "feed-1",
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )
        assertEquals(1779184800000L, result.publishedAtEpochMillis)
    }
    @Test
    fun `Given RSS item unparseable date When mapped to local post Then published time is null`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "not a date"),
            feedId = "feed-1",
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )
        assertEquals(null, result.publishedAtEpochMillis)
    }
    private fun createItem(pubDate: String?): ParsedFeedItem {
        return ParsedFeedItem(
            ordinal = 0,
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
