package dev.shounakmulay.devpulse.core.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

internal class KermitLogSinkWriter(
    private val sinks: List<LogSink>,
) : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        val entry = LogEntry(
            level = severity.toDPLogLevel(),
            tag = tag,
            message = message,
            throwable = throwable,
        )

        sinks
            .filter { sink -> sink.isLoggable(entry) }
            .forEach { sink -> sink.log(entry) }
    }
}
