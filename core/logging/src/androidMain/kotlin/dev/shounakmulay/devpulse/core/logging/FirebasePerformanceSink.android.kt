package dev.shounakmulay.devpulse.core.logging

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.logging.performance.DPPerformanceKey
import dev.shounakmulay.devpulse.core.logging.performance.PerformanceSink
import dev.shounakmulay.devpulse.core.logging.performance.PerformanceTrace
import dev.shounakmulay.devpulse.core.logging.performance.TraceEntry
import org.koin.core.annotation.Factory

@Factory
internal class FirebasePerformanceSink(
    private val performance: FirebasePerformance,
) : PerformanceSink {
    override fun isTraceable(entry: TraceEntry): Boolean = entry.name.wireName.isNotEmpty() && entry.name.wireName.length <= 100 &&
        entry.name.wireName == entry.name.wireName.trim() && !entry.name.wireName.startsWith("_")

    override fun startTrace(entry: TraceEntry): PerformanceTrace {
        val trace = performance.newTrace(entry.name.wireName)
        entry.data.forEach { (key, value) -> trace.putAttribute(key.wireName, value) }
        trace.putAttribute(DPPerformanceKey.ENVIRONMENT.wireName, BuildKonfigCommon.ENVIRONMENT)
        trace.start()
        return FirebasePerformanceTrace(trace)
    }
}

private class FirebasePerformanceTrace(private val trace: Trace) : PerformanceTrace {
    override fun stop() {
        trace.stop()
    }
}
