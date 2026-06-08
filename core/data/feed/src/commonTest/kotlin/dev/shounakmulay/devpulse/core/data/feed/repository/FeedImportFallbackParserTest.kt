package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import dev.shounakmulay.devpulse.core.logging.DPLog
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkResponse
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class FeedImportFallbackParserTest {
    @Test
    fun `Given primary succeeds When import runs Then XML is fetched once and fallback is not called`() = runTest {
        val primary = FakeCandidate(id = "prof18")
        val fallback = FakeCandidate(id = "ktxml")
        val networkClient = FakeNetworkClient(xml = RssXml)
        val parser = createParser(networkClient, primary, fallback)

        parser.import(createQueueEntry())

        assertEquals(listOf("https://example.com/feed.xml"), networkClient.requestedUrls)
        assertEquals(1, primary.callCount)
        assertEquals(0, fallback.callCount)
        assertEquals(listOf(RssXml), primary.xmlRequests)
    }

    @Test
    fun `Given primary fails When import runs Then XML is fetched once and fallback is called with same XML`() = runTest {
        val primary = FakeCandidate(id = "prof18", failure = IllegalStateException("primary"))
        val fallback = FakeCandidate(id = "ktxml")
        val networkClient = FakeNetworkClient(xml = RssXml)
        val parser = createParser(networkClient, primary, fallback)

        parser.import(createQueueEntry())

        assertEquals(listOf("https://example.com/feed.xml"), networkClient.requestedUrls)
        assertEquals(1, primary.callCount)
        assertEquals(1, fallback.callCount)
        assertEquals(listOf(RssXml), primary.xmlRequests)
        assertEquals(listOf(RssXml), fallback.xmlRequests)
    }

    @Test
    fun `Given all candidates fail When import runs Then final failure is thrown`() = runTest {
        val primary = FakeCandidate(id = "prof18", failure = IllegalStateException("primary"))
        val fallbackFailure = IllegalArgumentException("fallback")
        val fallback = FakeCandidate(id = "ktxml", failure = fallbackFailure)
        val parser = createParser(FakeNetworkClient(xml = RssXml), primary, fallback)

        val result = assertFailsWith<IllegalArgumentException> {
            parser.import(createQueueEntry())
        }

        assertEquals(fallbackFailure, result)
        assertEquals(1, primary.callCount)
        assertEquals(1, fallback.callCount)
    }

    @Test
    fun `Given feed fetch fails When import runs Then candidates are not called`() = runTest {
        val failure = IllegalStateException("network")
        val primary = FakeCandidate(id = "prof18")
        val fallback = FakeCandidate(id = "ktxml")
        val parser = createParser(
            networkClient = FakeNetworkClient(xml = RssXml, failure = failure),
            primary,
            fallback
        )

        val result = assertFailsWith<IllegalStateException> {
            parser.import(createQueueEntry())
        }

        assertSame(failure, result)
        assertEquals(0, primary.callCount)
        assertEquals(0, fallback.callCount)
    }

    private fun createParser(
        networkClient: DevPulseNetworkClient,
        primaryParser: FeedImportCandidate,
        secondaryParser: FeedImportCandidate
    ): FeedImportFallbackParser {
        return FeedImportFallbackParser(
            primaryParser = primaryParser,
            secondaryParser = secondaryParser,
            networkClient = networkClient,
            logger = DPLog.tag("FeedImportFallbackParserTest"),
        )
    }

    private class FakeCandidate(
        override val id: String,
        private val failure: Throwable? = null
    ) : FeedImportCandidate {
        var callCount = 0
            private set
        val xmlRequests = mutableListOf<String>()

        override suspend fun import(entry: RssFeedQueueEntry, xml: String) {
            callCount += 1
            xmlRequests += xml
            failure?.let { throw it }
        }

        override suspend fun import(entry: RssFeedQueueEntry, iterator: CharIterator) {
            import(entry = entry, xml = iterator.asSequence().joinToString(separator = ""))
        }
    }

    private class FakeNetworkClient(
        private val xml: String,
        private val failure: Throwable? = null
    ) : DevPulseNetworkClient {
        val requestedUrls = mutableListOf<String>()

        override suspend fun get(url: String): DevPulseNetworkResponse {
            requestedUrls += url
            failure?.let { throw it }
            return DevPulseNetworkResponse { xml }
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

    private companion object {
        const val RssXml = "<rss><channel><title>Example</title></channel></rss>"
    }
}
