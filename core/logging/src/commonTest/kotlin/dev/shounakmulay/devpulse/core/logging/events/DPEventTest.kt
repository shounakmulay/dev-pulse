package dev.shounakmulay.devpulse.core.logging.events

import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import kotlinx.coroutines.flow.flowOf
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DPEventTest {
    private companion object {
        val consent = TestConsent()
    }

    private class TestConsent : AnalyticsConsentProvider {
        var enabled: Boolean? = true
        override fun getConsent() = enabled?.let { DPAnalyticsConsent(it, it, it) }
        override fun observeConsent() = flowOf(getConsent())
    }

    @BeforeTest
    fun setUp() {
        consent.enabled = true
        startKoin { modules(module { single<AnalyticsConsentProvider> { consent } }) }
    }

    @AfterTest
    fun resetEvents() {
        consent.enabled = true
        DPEvent.configure(emptyList())
        stopKoin()
    }

    @Test
    fun `Given typed event and parameters When tracked Then wire names are unchanged`() {
        val sink = RecordingSink()
        DPEvent.configure(listOf(sink))

        DPEvent.track(DPEvents.POST_BOOKMARK_CHANGED, mapOf(DPEventKey.BOOKMARKED to "true"))

        val entry = sink.entries.single()
        assertEquals("post_bookmark_changed", entry.name.eventName)
        assertEquals(mapOf("bookmarked" to "true"), entry.data.mapKeys { it.key.wireName })
        assertEquals("article_origin", DPEventKey.ARTICLE_ORIGIN.wireName)
        assertEquals("source", DPEventKey.SOURCE.wireName)
        assertEquals("environment", DPEventKey.ENVIRONMENT.wireName)
        assertEquals(
            listOf(
                "post_bookmark_changed", "feed_deleted", "opml_extraction_completed", "opml_extraction_failed",
                "post_content_fetch_completed", "post_content_fetch_failed", "feed_import_completed", "feed_import_failed",
                "discovery_fetch_completed", "discovery_fetch_failed",
            ),
            DPEvents.entries.map { it.eventName },
        )
    }

    @Test
    fun `Given custom event data When tracking Then all sinks receive immutable snapshot`() {
        val first = RecordingSink()
        val second = RecordingSink()
        DPEvent.configure(listOf(first, second))
        val data = mutableMapOf(DPEventKey.SOURCE to "android")

        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED, data = data)
        data[DPEventKey.SOURCE] = "changed"

        assertEquals(EventEntry(name = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf(DPEventKey.SOURCE to "android")), first.entries.single())
        assertSame(first.entries.single(), second.entries.single())
    }

    @Test
    fun `Given filtering sink When tracking rejected and accepted events Then retains accepted payload`() {
        val sink = RecordingSink { it.name == DPEvents.FEED_IMPORT_COMPLETED }
        DPEvent.configure(listOf(sink))

        DPEvent.track(event = DPEvents.FEED_IMPORT_FAILED)
        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf(DPEventKey.SOURCE to "android"))

        assertEquals(listOf(EventEntry(name = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf(DPEventKey.SOURCE to "android"))), sink.entries)
    }

    @Test
    fun `Given failed filter and delivery When tracking Then later sink receives event`() {
        val failedFilter = object : EventSink {
            override fun isTrackable(entry: EventEntry): Boolean = throw IllegalStateException("Filter failed")
            override fun track(entry: EventEntry) = error("Must not reach delivery")
        }
        val failedDelivery = object : EventSink {
            override fun isTrackable(entry: EventEntry): Boolean = true
            override fun track(entry: EventEntry) = throw IllegalStateException("Delivery failed")
        }
        val working = RecordingSink()
        DPEvent.configure(listOf(failedFilter, failedDelivery, working))

        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf(DPEventKey.SOURCE to "android"))

        assertEquals(listOf(EventEntry(name = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf(DPEventKey.SOURCE to "android"))), working.entries)
    }

    @Test
    fun `Given cancellation in filter or delivery When tracking Then cancellation propagates`() {
        val cancellation = CancellationException("Cancelled")
        listOf(true, false).forEach { failInFilter ->
            DPEvent.configure(listOf(object : EventSink {
                override fun isTrackable(entry: EventEntry): Boolean {
                    if (failInFilter) throw cancellation
                    return true
                }
                override fun track(entry: EventEntry) { throw cancellation }
            }))

            val thrown = assertFailsWith<CancellationException> { DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED) }

            assertSame(cancellation, thrown)
        }
    }

    @Test
    fun `Given reconfigured events When tracking Then only replacement sink receives event`() {
        val previous = RecordingSink()
        val current = RecordingSink()
        val sinks = mutableListOf<EventSink>(current)
        DPEvent.configure(listOf(previous))
        DPEvent.configure(sinks)
        sinks.clear()

        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED)

        assertEquals(emptyList(), previous.entries)
        assertEquals(listOf(EventEntry(name = DPEvents.FEED_IMPORT_COMPLETED)), current.entries)
    }

    @Test
    fun `Given missing or rejected consent When tracking Then no events reach sink`() {
        val sink = RecordingSink()
        DPEvent.configure(listOf(sink))
        for (enabled in listOf(null, false)) {
            consent.enabled = enabled
            DPEvent.track(DPEvents.FEED_IMPORT_FAILED)
        }
        assertEquals(emptyList(), sink.entries)
    }

    private class RecordingSink(
        private val trackable: (EventEntry) -> Boolean = { true },
    ) : EventSink {
        val entries = mutableListOf<EventEntry>()
        override fun isTrackable(entry: EventEntry): Boolean = trackable(entry)
        override fun track(entry: EventEntry) { entries += entry }
    }
}
