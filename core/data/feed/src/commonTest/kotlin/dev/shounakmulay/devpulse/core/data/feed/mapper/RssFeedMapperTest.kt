package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.feed.identity.RssIdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedMetadata
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class RssFeedMapperTest {
    private val mapper = RssFeedMapper(
        identityGenerator = RssIdentityGenerator(),
        dateTimeProvider = FixedDateTimeProvider
    )
    @Test
    fun `Given RSS channel date When mapped to local feed Then raw publisher date is preserved`() {
        val result = mapper.toLocalRssFeed(
            from = createChannel(lastBuildDate = "Tue, 19 May 2026 10:00:00 GMT"),
            existingIdentity = LocalRssFeedIdentitySlice(
                id = "feed-1",
                title = "Title",
                pinned = false,
                sourceUrl = "https://example.com/feed.xml",
                link = "https://example.com",
                createdAt = 1234L,
                updatedAt = 5678L
            ),
            queueEntry = createQueueEntry()
        )
        assertEquals("Tue, 19 May 2026 10:00:00 GMT", result.lastBuildDate)
        assertEquals(1234L, result.createdAt)
        assertEquals(1779184800000L, result.updatedAt)
    }
    private fun createQueueEntry(): RssFeedQueueEntry {
        return RssFeedQueueEntry(
            url = "https://example.com/feed.xml",
            name = "Example",
            feedType = RssFeedType.CONTENT,
            actionType = RssFeedQueueActionType.IMPORT,
            requestor = RssFeedQueueActionRequestor.USER,
            status = RssFeedQueueStatus.QUEUED,
            tags = emptyList(),
            folders = emptyList(),
            createdAt = 1000L,
            updatedAt = 1000L,
        )
    }
    private fun createChannel(lastBuildDate: String?): ParsedFeedMetadata {
        return ParsedFeedMetadata(
            title = "Title",
            link = "https://example.com",
            description = "Description",
            image = null,
            lastBuildDate = lastBuildDate,
            updatePeriod = null,
            youtubeChannel = null
        )
    }
    private object FixedDateTimeProvider : DateTimeProvider {
        override fun now(): Long = nowEpochMilliseconds()
        override fun nowEpochMilliseconds(): Long = 1779184800000L
        override fun today(): LocalDate = LocalDate(year = 2026, monthNumber = 5, dayOfMonth = 19)
    }
}
