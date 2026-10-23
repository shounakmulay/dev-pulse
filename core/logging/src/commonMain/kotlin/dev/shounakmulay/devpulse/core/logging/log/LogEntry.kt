package dev.shounakmulay.devpulse.core.logging.log

data class LogEntry(
    val level: DPLogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable?,
    val data: Map<String, String> = emptyMap(),
) {
    val stackTrace: String? get() = throwable?.stackTraceToString()
}
