package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import dev.shounakmulay.devpulse.core.logging.DPLog
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FeedImportFallbackParserTest {
    @Test
    fun `Given primary succeeds When import runs Then fallback is not called`() = runTest {
        val primary = FakeCandidate(id = "prof18")
        val fallback = FakeCandidate(id = "ktxml")
        val parser = createParser(primary, fallback)

        parser.import(createQueueEntry())

        assertEquals(1, primary.callCount)
        assertEquals(0, fallback.callCount)
    }

    @Test
    fun `Given primary fails When import runs Then fallback is called`() = runTest {
        val primary = FakeCandidate(id = "prof18", failure = IllegalStateException("primary"))
        val fallback = FakeCandidate(id = "ktxml")
        val parser = createParser(primary, fallback)

        parser.import(createQueueEntry())

        assertEquals(1, primary.callCount)
        assertEquals(1, fallback.callCount)
    }

    @Test
    fun `Given all candidates fail When import runs Then final failure is thrown`() = runTest {
        val primary = FakeCandidate(id = "prof18", failure = IllegalStateException("primary"))
        val fallbackFailure = IllegalArgumentException("fallback")
        val fallback = FakeCandidate(id = "ktxml", failure = fallbackFailure)
        val parser = createParser(primary, fallback)

        val result = assertFailsWith<IllegalArgumentException> {
            parser.import(createQueueEntry())
        }

        assertEquals(fallbackFailure, result)
        assertEquals(1, primary.callCount)
        assertEquals(1, fallback.callCount)
    }

    private fun createParser(vararg candidates: FeedImportCandidate): FeedImportFallbackParser {
        return FeedImportFallbackParser(
            candidates = candidates.toList(),
            logger = DPLog.tag("FeedImportFallbackParserTest")
        )
    }

    private class FakeCandidate(
        override val id: String,
        private val failure: Throwable? = null
    ) : FeedImportCandidate {
        var callCount = 0
            private set

        override suspend fun import(entry: RssFeedQueueEntry) {
            callCount += 1
            failure?.let { throw it }
        }
    }

    private fun createQueueEntry(): RssFeedQueueEntry {
        return RssFeedQueueEntry(
            id = 1,
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
}
