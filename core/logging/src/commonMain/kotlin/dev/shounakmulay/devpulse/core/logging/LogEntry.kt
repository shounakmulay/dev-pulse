package dev.shounakmulay.devpulse.core.logging

data class LogEntry(
    val level: DPLogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable?,
)
