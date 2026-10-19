package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.logging.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.LogEntry
import dev.shounakmulay.devpulse.core.logging.LogSink
import dev.shounakmulay.devpulse.core.telemetry.api.ErrorReporter
import dev.shounakmulay.devpulse.core.telemetry.api.SafeBreadcrumb
import kotlinx.collections.immutable.toPersistentList

data class SafeLogMapping(val tag: String, val minimumLevel: DPLogLevel, val breadcrumb: SafeBreadcrumb)

class TelemetryLogSink(
    private val errorReporter: ErrorReporter,
    mappings: List<SafeLogMapping> = emptyList(),
) : LogSink {
    private val mappings = mappings.toPersistentList()
    override fun isLoggable(entry: LogEntry): Boolean = mappings.any { entry.tag == it.tag && entry.level >= it.minimumLevel }
    override fun log(entry: LogEntry) {
        mappings.filter { entry.tag == it.tag && entry.level >= it.minimumLevel }.forEach {
            errorReporter.breadcrumb(it.breadcrumb)
        }
    }
}
