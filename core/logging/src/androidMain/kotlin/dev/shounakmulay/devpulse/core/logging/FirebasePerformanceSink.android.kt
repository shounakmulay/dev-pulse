package dev.shounakmulay.devpulse.core.logging

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import dev.shounakmulay.devpulse.core.logging.performance.PerformanceSink
import dev.shounakmulay.devpulse.core.logging.performance.PerformanceTrace
import dev.shounakmulay.devpulse.core.logging.performance.TraceEntry
import org.koin.core.annotation.Factory

@Factory
internal class FirebasePerformanceSink(
    private val performance: FirebasePerformance,
) : PerformanceSink {
    override fun isTraceable(entry: TraceEntry): Boolean =
        entry.name.isNotEmpty() && entry.name.length <= 100 &&
            entry.name == entry.name.trim() && !entry.name.startsWith("_")

    override fun startTrace(entry: TraceEntry): PerformanceTrace {
        val trace = performance.newTrace(entry.name)
        entry.data.forEach { (key, value) -> trace.putAttribute(key, value) }
        trace.start()
        return FirebasePerformanceTrace(trace)
    }
}

private class FirebasePerformanceTrace(private val trace: Trace) : PerformanceTrace {
    override fun stop() {
        trace.stop()
    }
}
