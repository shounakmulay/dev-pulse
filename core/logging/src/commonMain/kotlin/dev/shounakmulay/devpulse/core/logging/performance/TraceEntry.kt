package dev.shounakmulay.devpulse.core.logging.performance

data class TraceEntry(
    val name: String,
    val data: Map<String, String> = emptyMap(),
)
