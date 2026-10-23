package dev.shounakmulay.devpulse.core.logging.events

import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DPEventTest {
    @AfterTest
    fun resetEvents() {
        DPEvent.configure(emptyList())
    }

    @Test
    fun `Given custom event data When tracking Then all sinks receive immutable snapshot`() {
        val first = RecordingSink()
        val second = RecordingSink()
        DPEvent.configure(listOf(first, second))
        val data = mutableMapOf("collection_id" to "android")

        DPEvent.track(name = "collection_opened", data = data)
        data["collection_id"] = "changed"

        assertEquals(EventEntry(name = "collection_opened", data = mapOf("collection_id" to "android")), first.entries.single())
        assertSame(first.entries.single(), second.entries.single())
    }

    @Test
    fun `Given filtering sink When tracking rejected and accepted events Then retains accepted payload`() {
        val sink = RecordingSink { it.name == "collection_opened" }
        DPEvent.configure(listOf(sink))

        DPEvent.track(name = "ignored")
        DPEvent.track(name = "collection_opened", data = mapOf("collection_id" to "android"))

        assertEquals(listOf(EventEntry(name = "collection_opened", data = mapOf("collection_id" to "android"))), sink.entries)
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

        DPEvent.track(name = "collection_opened", data = mapOf("collection_id" to "android"))

        assertEquals(listOf(EventEntry(name = "collection_opened", data = mapOf("collection_id" to "android"))), working.entries)
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

            val thrown = assertFailsWith<CancellationException> { DPEvent.track(name = "collection_opened") }

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

        DPEvent.track(name = "collection_opened")

        assertEquals(emptyList(), previous.entries)
        assertEquals(listOf(EventEntry(name = "collection_opened")), current.entries)
    }

    private class RecordingSink(
        private val trackable: (EventEntry) -> Boolean = { true },
    ) : EventSink {
        val entries = mutableListOf<EventEntry>()
        override fun isTrackable(entry: EventEntry): Boolean = trackable(entry)
        override fun track(entry: EventEntry) { entries += entry }
    }
}
