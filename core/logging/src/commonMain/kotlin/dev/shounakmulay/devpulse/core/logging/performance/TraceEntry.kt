package dev.shounakmulay.devpulse.core.logging.performance

data class TraceEntry(
    val name: DPPerformanceTrace,
    val data: Map<DPPerformanceKey, String> = emptyMap(),
)
