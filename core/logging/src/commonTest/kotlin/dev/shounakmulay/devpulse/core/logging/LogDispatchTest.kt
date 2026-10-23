package dev.shounakmulay.devpulse.core.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.logging.log.DPLogConfig
import dev.shounakmulay.devpulse.core.logging.log.DPLogConfigs
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.log.LogEntry
import dev.shounakmulay.devpulse.core.logging.log.LogSink
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class LogDispatchTest {
    @AfterTest
    fun resetLogging() {
        DPLog.configure(DPLogConfigs.debug())
    }

    @Test
    fun `Given custom data When logging through both APIs Then preserves copied payloads`() {
        val sink = TestLogSink()
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(sink)))
        val data = mutableMapOf("feed_id" to "feed-1")
        val failure = IllegalStateException("Import failed")

        DPLog.tag("FeedImport").e(throwable = failure, data = data) { "Unable to import" }
        DPLog.i(tag = "Sync", data = mapOf("feed_id" to "feed-2")) { "Synced" }
        data["feed_id"] = "changed"

        assertEquals(listOf("FeedImport", "Sync"), sink.entries.map { it.tag })
        assertEquals(listOf("Unable to import", "Synced"), sink.entries.map { it.message })
        assertEquals(listOf(mapOf("feed_id" to "feed-1"), mapOf("feed_id" to "feed-2")), sink.entries.map { it.data })
        assertSame(failure, sink.entries.first().throwable)
    }

    @Test
    fun `Given multiple sinks When dispatching rich entry Then evaluates message once and retains console`() {
        val first = TestLogSink()
        val second = TestLogSink()
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(first, second)))
        val console = mutableListOf<String>()
        val failure = IllegalStateException("Failure")
        Logger.setLogWriters(object : LogWriter() {
            override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
                console += "$severity/$tag/$message"
                assertSame(failure, throwable)
            }
        })
        var evaluations = 0

        DPLog.e(tag = "Import", throwable = failure, data = mapOf("job" to "42")) {
            evaluations++
            "Failed"
        }

        assertEquals(1, evaluations)
        assertEquals(listOf("Error/Import/Failed"), console)
        assertEquals(mapOf("job" to "42"), first.singleEntry().data)
        assertSame(first.singleEntry(), second.singleEntry())
    }

    @Test
    fun `Given failed filter and delivery When logging Then later sink still receives payload`() {
        val brokenFilter = object : LogSink {
            override fun isLoggable(entry: LogEntry): Boolean = throw IllegalStateException("Filter failed")
            override fun log(entry: LogEntry) = error("Must not reach delivery")
        }
        val brokenDelivery = object : LogSink {
            override fun isLoggable(entry: LogEntry): Boolean = true
            override fun log(entry: LogEntry) = throw IllegalStateException("Delivery failed")
        }
        val working = TestLogSink()
        DPLog.configure(
            DPLogConfig(
                minLevel = DPLogLevel.Verbose,
                sinks = listOf(brokenFilter, brokenDelivery, working)
            )
        )

        DPLog.e(tag = "Import", data = mapOf("job" to "42")) { "Failed" }

        assertEquals("Failed", working.singleEntry().message)
        assertEquals(mapOf("job" to "42"), working.singleEntry().data)
    }

    @Test
    fun `Given cancellation in filter or delivery When logging Then cancellation propagates`() {
        val cancellation = CancellationException("Cancelled")
        listOf(true, false).forEach { failInFilter ->
            val sink = object : LogSink {
                override fun isLoggable(entry: LogEntry): Boolean {
                    if (failInFilter) throw cancellation
                    return true
                }
                override fun log(entry: LogEntry) { throw cancellation }
            }
            DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(sink)))

            val thrown = assertFailsWith<CancellationException> {
                DPLog.e(tag = "Import") { "Failed" }
            }

            assertSame(cancellation, thrown)
        }
    }

    @Test
    fun `Given mutable sink configuration When caller clears list Then configured sink still receives entry`() {
        val sink = TestLogSink()
        val sinks = mutableListOf<LogSink>(sink)
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = sinks))
        sinks.clear()

        DPLog.e(tag = "Import") { "Failed" }

        assertEquals("Failed", sink.singleEntry().message)
    }
}
