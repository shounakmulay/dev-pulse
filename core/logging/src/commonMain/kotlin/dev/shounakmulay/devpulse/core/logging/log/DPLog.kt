package dev.shounakmulay.devpulse.core.logging.log

import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException

object DPLog {
    @Volatile
    private var configuration: DPLogConfig = DPLogConfigs.debug()

    fun configure(config: DPLogConfig) {
        configuration = config
        Logger.setMinSeverity(config.minLevel.toSeverity())
        Logger.setLogWriters(platformLogWriter())
    }

    fun tag(tag: String): DPLogger = DPLogger(tag)

    fun tag(owner: Any): DPLogger = tag(owner::class.simpleName ?: UNKNOWN_TAG)

    fun v(
        tag: String,
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        log(
            level = DPLogLevel.Verbose,
            tag = tag,
            throwable = throwable,
            data = data,
            message = message,
        )
    }

    fun d(
        tag: String,
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        log(
            level = DPLogLevel.Debug,
            tag = tag,
            throwable = throwable,
            data = data,
            message = message,
        )
    }

    fun i(
        tag: String,
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        log(
            level = DPLogLevel.Info,
            tag = tag,
            throwable = throwable,
            data = data,
            message = message,
        )
    }

    fun w(
        tag: String,
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        log(
            level = DPLogLevel.Warn,
            tag = tag,
            throwable = throwable,
            data = data,
            message = message,
        )
    }

    fun e(
        tag: String,
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        log(
            level = DPLogLevel.Error,
            tag = tag,
            throwable = throwable,
            data = data,
            message = message,
        )
    }

    fun a(
        tag: String,
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        log(
            level = DPLogLevel.Assert,
            tag = tag,
            throwable = throwable,
            data = data,
            message = message,
        )
    }

    private fun log(
        level: DPLogLevel,
        tag: String,
        throwable: Throwable?,
        data: Map<String, String>,
        message: () -> String,
    ) {
        val config = configuration
        if (level < config.minLevel) return

        when (level) {
            DPLogLevel.Verbose -> Logger.v(tag = tag, throwable = throwable, message = message)

            DPLogLevel.Debug -> Logger.d(
                tag = tag,
                throwable = throwable,
                message = message,
            )

            DPLogLevel.Info -> Logger.i(
                tag = tag,
                throwable = throwable,
                message = message,
            )

            DPLogLevel.Warn -> Logger.w(
                tag = tag,
                throwable = throwable,
                message = message,
            )

            DPLogLevel.Error -> Logger.e(
                tag = tag,
                throwable = throwable,
                message = message,
            )

            DPLogLevel.Assert -> Logger.a(
                tag = tag,
                throwable = throwable,
                message = message,
            )
        }
        config.sinks.forEach { sink ->
            try {
                val entry = LogEntry(
                    level = level,
                    tag = tag,
                    message = message(),
                    throwable = throwable,
                    data = data,
                )
                if (sink.isLoggable(entry)) sink.log(entry)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
            }
        }
    }
}

internal const val UNKNOWN_TAG = "Unknown"
