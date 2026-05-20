package dev.shounakmulay.devpulse.core.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.platformLogWriter

object DPLog {
    fun configure(config: DPLogConfig) {
        Logger.setMinSeverity(config.minLevel.toSeverity())
        Logger.setLogWriters(config.logWriters())
    }

    fun tag(tag: String): DPLogger = DPLogger(tag)

    fun tag(owner: Any): DPLogger = tag(owner::class.simpleName ?: UnknownTag)

    fun v(tag: String, throwable: Throwable? = null, message: () -> String) {
        Logger.v(tag = tag, throwable = throwable, message = message)
    }

    fun d(tag: String, throwable: Throwable? = null, message: () -> String) {
        Logger.d(tag = tag, throwable = throwable, message = message)
    }

    fun i(tag: String, throwable: Throwable? = null, message: () -> String) {
        Logger.i(tag = tag, throwable = throwable, message = message)
    }

    fun w(tag: String, throwable: Throwable? = null, message: () -> String) {
        Logger.w(tag = tag, throwable = throwable, message = message)
    }

    fun e(tag: String, throwable: Throwable? = null, message: () -> String) {
        Logger.e(tag = tag, throwable = throwable, message = message)
    }

    fun a(tag: String, throwable: Throwable? = null, message: () -> String) {
        Logger.a(tag = tag, throwable = throwable, message = message)
    }

    private fun DPLogConfig.logWriters(): List<LogWriter> = buildList {
        add(platformLogWriter())

        if (sinks.isNotEmpty()) {
            add(KermitLogSinkWriter(sinks))
        }
    }
}

internal const val UnknownTag = "Unknown"
