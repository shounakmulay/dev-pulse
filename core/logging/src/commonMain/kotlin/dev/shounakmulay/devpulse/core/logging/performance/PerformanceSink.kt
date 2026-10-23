package dev.shounakmulay.devpulse.core.logging.performance

interface PerformanceSink {
    fun isTraceable(entry: TraceEntry): Boolean

    fun startTrace(entry: TraceEntry): PerformanceTrace
}
