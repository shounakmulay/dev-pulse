package dev.shounakmulay.devpulse.core.logging.performance

import org.koin.core.annotation.Single
import kotlin.concurrent.Volatile
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@Single
object DPPerformance {
    @Volatile
    private var sinks: List<PerformanceSink>? = null

    fun configure(sinks: List<PerformanceSink>) {
        this.sinks = sinks
    }

    fun startTrace(
        trace: DPPerformanceTrace,
        data: Map<String, String> = emptyMap()
    ): PerformanceTrace {
        val configuredSinks = requireNotNull(sinks) {
            "DPPerformance configuration is missing. Did you call DPPerformance.configure()"
        }
        val entry = TraceEntry(name = trace.name, data = data)
        val traces = buildList {
            configuredSinks.forEach { sink ->
                try {
                    if (sink.isTraceable(entry)) add(sink.startTrace(entry))
                } catch (_: Exception) {
                }
            }
        }
        return ActivePerformanceTrace(traces)
    }

    inline fun <T> measure(
        trace: DPPerformanceTrace,
        data: Map<String, String> = emptyMap(),
        block: () -> T,
    ): T {
        val trace = startTrace(trace = trace, data = data)
        try {
            return block()
        } finally {
            trace.stop()
        }
    }
}

@OptIn(ExperimentalAtomicApi::class)
private class ActivePerformanceTrace(traces: List<PerformanceTrace>) : PerformanceTrace {
    private val activeTraces = AtomicReference<List<PerformanceTrace>?>(traces)

    override fun stop() {
        activeTraces.exchange(null)?.forEach { trace ->
            try {
                trace.stop()
            } catch (_: Exception) {
            }
        }
    }
}
