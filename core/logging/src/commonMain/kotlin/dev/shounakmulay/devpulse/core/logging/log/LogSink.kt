package dev.shounakmulay.devpulse.core.logging.log

interface LogSink {
    fun isLoggable(entry: LogEntry): Boolean

    fun log(entry: LogEntry)
}
