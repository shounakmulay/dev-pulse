package dev.shounakmulay.devpulse.core.logging

import co.touchlab.kermit.Severity
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DPLogTest {
    @AfterTest
    fun resetLogger() {
        DPLog.configure(DPLogConfigs.debug())
    }

    @Test
    fun `Given tagged logger When logging info Then forwards correct tag`() {
        val sink = TestLogSink()
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(sink)))

        DPLog.tag("FeedViewModel").i { "Feed screen opened" }

        assertEquals("FeedViewModel", sink.singleEntry().tag)
        assertEquals("Feed screen opened", sink.singleEntry().message)
        assertEquals(DPLogLevel.Info, sink.singleEntry().level)
    }

    @Test
    fun `Given static logger When logging debug Then forwards correct tag`() {
        val sink = TestLogSink()
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(sink)))

        DPLog.d(tag = "DevPulseHttpClient") { "Creating HTTP client" }

        assertEquals("DevPulseHttpClient", sink.singleEntry().tag)
        assertEquals("Creating HTTP client", sink.singleEntry().message)
        assertEquals(DPLogLevel.Debug, sink.singleEntry().level)
    }

    @Test
    fun `Given Kermit writer When receiving severities Then maps to DP log levels`() {
        val sink = TestLogSink()
        val writer = KermitLogSinkWriter(listOf(sink))

        writer.log(Severity.Verbose, "verbose", "Tag", null)
        writer.log(Severity.Debug, "debug", "Tag", null)
        writer.log(Severity.Info, "info", "Tag", null)
        writer.log(Severity.Warn, "warn", "Tag", null)
        writer.log(Severity.Error, "error", "Tag", null)
        writer.log(Severity.Assert, "assert", "Tag", null)

        assertEquals(
            listOf(
                DPLogLevel.Verbose,
                DPLogLevel.Debug,
                DPLogLevel.Info,
                DPLogLevel.Warn,
                DPLogLevel.Error,
                DPLogLevel.Assert,
            ),
            sink.entries.map { entry -> entry.level },
        )
    }

    @Test
    fun `Given disabled level When logging below minimum Then does not evaluate message`() {
        val sink = TestLogSink()
        var evaluated = false
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Warn, sinks = listOf(sink)))

        DPLog.d(tag = "Disabled") {
            evaluated = true
            "This should not be evaluated"
        }

        assertFalse(evaluated)
        assertTrue(sink.entries.isEmpty())
    }

    @Test
    fun `Given throwable When logging error Then preserves throwable`() {
        val sink = TestLogSink()
        val throwable = IllegalStateException("Broken")
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(sink)))

        DPLog.e(tag = "RssFeedParser", throwable = throwable) { "Failed to parse feed" }

        assertSame(throwable, sink.singleEntry().throwable)
    }

    @Test
    fun `Given multiple sinks When logging warning Then all sinks receive entry`() {
        val firstSink = TestLogSink()
        val secondSink = TestLogSink()
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(firstSink, secondSink)))

        DPLog.w(tag = "Queue") { "Retry scheduled" }

        assertEquals("Retry scheduled", firstSink.singleEntry().message)
        assertEquals("Retry scheduled", secondSink.singleEntry().message)
    }

    @Test
    fun `Given filtering sink When entry is not loggable Then skips sink`() {
        val sink = TestLogSink(loggable = { entry -> entry.level >= DPLogLevel.Error })
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(sink)))

        DPLog.w(tag = "Queue") { "Retry scheduled" }
        DPLog.e(tag = "Queue") { "Retry failed" }

        assertEquals(1, sink.entries.size)
        assertEquals("Retry failed", sink.singleEntry().message)
    }

    @Test
    fun `Given logger is reconfigured When logging Then old sinks no longer receive entries`() {
        val oldSink = TestLogSink()
        val newSink = TestLogSink()
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(oldSink)))
        DPLog.configure(DPLogConfig(minLevel = DPLogLevel.Verbose, sinks = listOf(newSink)))

        DPLog.i(tag = "Startup") { "Configured" }

        assertTrue(oldSink.entries.isEmpty())
        assertEquals("Configured", newSink.singleEntry().message)
    }
}
