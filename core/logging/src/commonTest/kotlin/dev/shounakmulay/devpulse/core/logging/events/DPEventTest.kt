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
    fun `Given custom event data When tracking Then all sinks receive immutable snapshot`() {
        val first = RecordingSink()
        val second = RecordingSink()
        DPEvent.configure(listOf(first, second))
        val data = mutableMapOf("collection_id" to "android")

        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED, data = data)
        data["collection_id"] = "changed"

        assertEquals(EventEntry(name = "feed_import_completed", data = mapOf("collection_id" to "android")), first.entries.single())
        assertSame(first.entries.single(), second.entries.single())
    }

    @Test
    fun `Given filtering sink When tracking rejected and accepted events Then retains accepted payload`() {
        val sink = RecordingSink { it.name == "feed_import_completed" }
        DPEvent.configure(listOf(sink))

        DPEvent.track(event = DPEvents.FEED_IMPORT_FAILED)
        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf("collection_id" to "android"))

        assertEquals(listOf(EventEntry(name = "feed_import_completed", data = mapOf("collection_id" to "android"))), sink.entries)
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

        DPEvent.track(event = DPEvents.FEED_IMPORT_COMPLETED, data = mapOf("collection_id" to "android"))

        assertEquals(listOf(EventEntry(name = "feed_import_completed", data = mapOf("collection_id" to "android"))), working.entries)
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
        assertEquals(listOf(EventEntry(name = "feed_import_completed")), current.entries)
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
