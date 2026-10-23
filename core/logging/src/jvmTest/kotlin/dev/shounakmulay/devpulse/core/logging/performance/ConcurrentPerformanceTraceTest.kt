package dev.shounakmulay.devpulse.core.logging.performance

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConcurrentPerformanceTraceTest {
    @AfterTest
    fun resetPerformance() {
        DPPerformance.configure(emptyList())
    }

    @Test
    fun `Given shared handle When stopping concurrently Then provider closes once`() {
        val stops = AtomicInteger()
        DPPerformance.configure(listOf(object : PerformanceSink {
            override fun isTraceable(entry: TraceEntry): Boolean = true
            override fun startTrace(entry: TraceEntry): PerformanceTrace = object : PerformanceTrace {
                override fun stop() { stops.incrementAndGet() }
            }
        }))
        val trace = DPPerformance.startTrace(name = "feed_import")
        val executor = Executors.newFixedThreadPool(8)
        val start = CountDownLatch(1)
        try {
            val tasks = List(8) {
                executor.submit {
                    start.await()
                    trace.stop()
                }
            }
            start.countDown()
            tasks.forEach { it.get(5, TimeUnit.SECONDS) }

            assertEquals(1, stops.get())
        } finally {
            executor.shutdownNow()
        }
    }
}
