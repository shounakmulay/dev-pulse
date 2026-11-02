package dev.shounakmulay.devpulse.core.logging.performance

import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import kotlinx.coroutines.flow.flowOf
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DPPerformanceTest {

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(module {
                single<AnalyticsConsentProvider> {
                    object : AnalyticsConsentProvider {
                        override fun getConsent() = DPAnalyticsConsent(performanceMetrics = true)
                        override fun observeConsent() = flowOf(getConsent())
                    }
                }
            })
        }
    }

    @AfterTest
    fun resetPerformance() {
        DPPerformance.configure(emptyList())
        stopKoin()
    }

    @Test
    fun `Given trace metadata When starting and stopping Then provider receives copied entry and one stop`() {
        val sink = RecordingSink()
        val sinks = mutableListOf<PerformanceSink>(sink)
        val data = mutableMapOf(DPPerformanceKey.FEED_ID to "feed-1")
        DPPerformance.configure(sinks)
        sinks.clear()

        val trace = DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_IMPORT, data = data)
        data[DPPerformanceKey.FEED_ID] = "changed"
        trace.stop()
        trace.stop()

        assertEquals(TraceEntry(name = DPPerformanceTrace.FEED_IMPORT, data = mapOf(DPPerformanceKey.FEED_ID to "feed-1")), sink.traces.single().entry)
        assertEquals("feed_import", sink.traces.single().entry.name.wireName)
        assertEquals(mapOf("feed_id" to "feed-1"), sink.traces.single().entry.data.mapKeys { it.key.wireName })
        assertEquals(1, sink.traces.single().stops)
    }

    @Test
    fun `Given sink filter When starting rejected and accepted traces Then only accepted trace runs`() {
        val sink = RecordingSink { it.name == DPPerformanceTrace.FEED_IMPORT }
        DPPerformance.configure(listOf(sink))

        DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_SYNC).stop()
        DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_IMPORT).stop()

        assertEquals(listOf(DPPerformanceTrace.FEED_IMPORT), sink.traces.map { it.entry.name })
        assertEquals(1, sink.traces.single().stops)
    }

    @Test
    fun `Given overlapping same-name traces When stopping out of order Then lifetimes remain independent`() {
        val sink = RecordingSink()
        DPPerformance.configure(listOf(sink))
        val first = DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_IMPORT)
        val second = DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_IMPORT)

        second.stop()
        assertEquals(listOf(0, 1), sink.traces.map { it.stops })
        first.stop()

        assertEquals(listOf(1, 1), sink.traces.map { it.stops })
    }

    @Test
    fun `Given reconfigured sinks When closing active trace Then original provider closes`() {
        val original = RecordingSink()
        val replacement = RecordingSink()
        DPPerformance.configure(listOf(original))
        val trace = DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_IMPORT)
        DPPerformance.configure(listOf(replacement))

        trace.stop()
        DPPerformance.startTrace(trace = DPPerformanceTrace.FEED_SYNC).stop()

        assertEquals(listOf(DPPerformanceTrace.FEED_IMPORT), original.traces.map { it.entry.name })
        assertEquals(listOf(DPPerformanceTrace.FEED_SYNC), replacement.traces.map { it.entry.name })
        assertEquals(1, original.traces.single().stops)
        assertEquals(1, replacement.traces.single().stops)
    }

    @Test
    fun `Given providers fail or cancel filter start and stop When measuring Then result and healthy trace survive`() {
        listOf(IllegalStateException("Provider unavailable"), CancellationException("Provider cancelled")).forEach { failure ->
            val filterFailure = object : PerformanceSink {
                override fun isTraceable(entry: TraceEntry): Boolean = throw failure
                override fun startTrace(entry: TraceEntry): PerformanceTrace = error("Must not start")
            }
            val startFailure = object : PerformanceSink {
                override fun isTraceable(entry: TraceEntry): Boolean = true
                override fun startTrace(entry: TraceEntry): PerformanceTrace = throw failure
            }
            val stopFailure = object : PerformanceSink {
                override fun isTraceable(entry: TraceEntry): Boolean = true
                override fun startTrace(entry: TraceEntry): PerformanceTrace = object : PerformanceTrace {
                    override fun stop() { throw failure }
                }
            }
            val healthy = RecordingSink()
            DPPerformance.configure(listOf(filterFailure, startFailure, stopFailure, healthy))

            val result = DPPerformance.measure(trace = DPPerformanceTrace.FEED_IMPORT) { 42 }

            assertEquals(42, result)
            assertEquals(1, healthy.traces.single().stops)
        }
    }

    @Test
    fun `Given measured work fails or cancels When finishing Then original failure propagates and all traces stop`() {
        listOf(IllegalStateException("Import failed"), CancellationException("Work cancelled")).forEach { failure ->
            val first = RecordingSink()
            val second = RecordingSink()
            val brokenStop = object : PerformanceSink {
                override fun isTraceable(entry: TraceEntry): Boolean = true
                override fun startTrace(entry: TraceEntry): PerformanceTrace = object : PerformanceTrace {
                    override fun stop() { throw CancellationException("Provider cancelled") }
                }
            }
            DPPerformance.configure(listOf(first, brokenStop, second))

            val thrown = assertFailsWith<Exception> {
                DPPerformance.measure(trace = DPPerformanceTrace.FEED_IMPORT) { throw failure }
            }

            assertSame(failure, thrown)
            assertEquals(1, first.traces.single().stops)
            assertEquals(1, second.traces.single().stops)
        }
    }

    @Test
    fun `Given suspended work When resuming Then trace covers suspension and returns original result`() {
        val sink = RecordingSink()
        DPPerformance.configure(listOf(sink))
        var pending: Continuation<String>? = null
        var outcome: Result<String>? = null
        val work: suspend () -> String = {
            DPPerformance.measure(trace = DPPerformanceTrace.FEED_IMPORT) {
                suspendCoroutine { pending = it }
            }
        }
        work.startCoroutine(object : Continuation<String> {
            override val context = EmptyCoroutineContext
            override fun resumeWith(result: Result<String>) { outcome = result }
        })
        assertEquals(0, sink.traces.single().stops)

        checkNotNull(pending).resume("Imported")

        assertEquals("Imported", checkNotNull(outcome).getOrThrow())
        assertEquals(1, sink.traces.single().stops)
    }

    @Test
    fun `Given suspended work When cancelled Then original cancellation propagates after trace stops`() {
        val sink = RecordingSink()
        DPPerformance.configure(listOf(sink))
        val cancellation = CancellationException("Import cancelled")
        var pending: Continuation<String>? = null
        var outcome: Result<String>? = null
        val work: suspend () -> String = {
            DPPerformance.measure(trace = DPPerformanceTrace.FEED_IMPORT) {
                suspendCoroutine { pending = it }
            }
        }
        work.startCoroutine(object : Continuation<String> {
            override val context = EmptyCoroutineContext
            override fun resumeWith(result: Result<String>) { outcome = result }
        })

        checkNotNull(pending).resumeWithException(cancellation)

        assertSame(cancellation, checkNotNull(outcome).exceptionOrNull())
        assertEquals(1, sink.traces.single().stops)
    }

    @Test
    fun `Given no configured sink When measuring Then business result is unchanged`() {
        DPPerformance.configure(emptyList())

        val result = DPPerformance.measure(trace = DPPerformanceTrace.FEED_IMPORT) { "Imported" }

        assertEquals("Imported", result)
    }

    private class RecordingSink(
        private val traceable: (TraceEntry) -> Boolean = { true },
    ) : PerformanceSink {
        val traces = mutableListOf<RecordingTrace>()
        override fun isTraceable(entry: TraceEntry): Boolean = traceable(entry)
        override fun startTrace(entry: TraceEntry): PerformanceTrace = RecordingTrace(entry).also { traces += it }
    }

    private class RecordingTrace(val entry: TraceEntry) : PerformanceTrace {
        var stops = 0
        override fun stop() { stops++ }
    }
}
