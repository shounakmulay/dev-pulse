package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.RssIdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeComponents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Instant

class RssPostMapperTest {
    private val uuidMapper = UuidMapper()
    private val mapper = RssPostMapper(
        idGenerator = RssIdentityGenerator(),
        dateTimeProvider = FixedDateTimeProvider,
        uuidMapper = uuidMapper
    )
    @Test
    fun `Given RSS item date When mapped to local post Then raw publisher date is preserved`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "Tue, 19 May 2026 10:00:00 +0000"),
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            existingIdentity = LocalRssContentFeedPostIdentitySlice(
                id = "post-1",
                fingerprint = "fingerprint-1",
                bookmarked = false,
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
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )
        assertEquals(1779184800000L, result.publishedAtEpochMillis)
    }
    @Test
    fun `Given RSS item offset date When mapped to local post Then published time is normalized to UTC`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "Tue, 19 May 2026 15:30:00 +0530"),
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )
        assertEquals(1779184800000L, result.publishedAtEpochMillis)
    }
    @Test
    fun `Given RSS item unparseable date When mapped to local post Then published time is null`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = "not a date"),
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )
        assertEquals(null, result.publishedAtEpochMillis)
    }
@Test
    fun `Given bookmarked existing identity When mapped to local post Then bookmark is preserved`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = null),
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            existingIdentity = LocalRssContentFeedPostIdentitySlice(
                id = "post-1",
                fingerprint = "fingerprint-1",
                bookmarked = true,
                createdAt = 1234L,
                updatedAt = 5678L
            )
        )

        assertEquals(true, result.bookmarked)
    }

    @Test
    fun `Given missing existing identity When mapped to local post Then bookmark is false`() {
        val result = mapper.toLocalRssContentFeedPost(
            item = createItem(pubDate = null),
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            existingIdentity = null
        )

        assertEquals(false, result.bookmarked)
    }

    @Test
    fun `Given bookmarked local post When mapped to domain post Then bookmark is preserved`() {
        val result = mapper.toRssFeedPost(
            from = createLocalPost(bookmarked = true)
        )

        assertEquals(true, result.bookmarked)
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

    private fun createLocalPost(bookmarked: Boolean): LocalRssContentFeedPost {
        return LocalRssContentFeedPost(
            id = uuidMapper.fromString("post-1"),
            feedId = uuidMapper.fromString("feed-1"),
            fingerprint = "fingerprint-1",
            guid = "guid-1",
            title = "Title",
            author = "Author",
            link = "https://example.com/post",
            pubDate = null,
            publishedAtEpochMillis = Long.MIN_VALUE,
            description = "Description",
            content = "Content",
            image = null,
            audio = null,
            video = null,
            sourceName = "Source",
            sourceUrl = "https://example.com/feed.xml",
            categories = "",
            commentsUrl = null,
            bookmarked = bookmarked,
            youtubeData = null,
            rawEnclosure = null,
            rawMedia = null,
            createdAt = 1234L,
            updatedAt = 5678L
        )
    }

    private object FixedDateTimeProvider : DateTimeProvider {
        override fun parse(string: String): Instant? {
            return runCatching {
                DateTimeComponents.Formats.RFC_1123.parse(string).toInstantUsingOffset()
            }.getOrNull()
        }

        override fun getTimeElapsed(instant: Instant): Duration = Duration.ZERO

        override fun now(): Instant = Instant.fromEpochMilliseconds( nowEpochMilliseconds())
        override fun nowEpochMilliseconds(): Long = 1779184800000L
        override fun today(): LocalDate = LocalDate(year = 2026, monthNumber = 5, dayOfMonth = 19)
    }
}
