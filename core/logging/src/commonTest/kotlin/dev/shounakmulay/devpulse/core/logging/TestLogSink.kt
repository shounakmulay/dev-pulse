package dev.shounakmulay.devpulse.core.logging

import dev.shounakmulay.devpulse.core.logging.log.LogEntry
import dev.shounakmulay.devpulse.core.logging.log.LogSink

internal class TestLogSink(
    private val loggable: (LogEntry) -> Boolean = { true },
) : LogSink {
    val entries = mutableListOf<LogEntry>()

    override fun isLoggable(entry: LogEntry): Boolean = loggable(entry)

    override fun log(entry: LogEntry) {
        entries += entry
    }

    fun singleEntry(): LogEntry = entries.single()
}
