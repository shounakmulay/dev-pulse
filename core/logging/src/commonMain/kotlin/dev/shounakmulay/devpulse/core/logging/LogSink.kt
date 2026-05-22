package dev.shounakmulay.devpulse.core.logging

interface LogSink {
    fun isLoggable(entry: LogEntry): Boolean

    fun log(entry: LogEntry)
}
