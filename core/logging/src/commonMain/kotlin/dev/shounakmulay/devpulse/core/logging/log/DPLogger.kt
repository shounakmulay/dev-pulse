package dev.shounakmulay.devpulse.core.logging.log

import org.koin.core.annotation.Factory

@Factory
class DPLogger internal constructor(
    private var tag: String = "DevPulse",
) {
    fun withTag(tag: String): DPLogger {
        this.tag = tag
        return this
    }

    fun v(
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        DPLog.v(tag = tag, throwable = throwable, data = data, message = message)
    }

    fun d(
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        DPLog.d(tag = tag, throwable = throwable, data = data, message = message)
    }

    fun i(
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        DPLog.i(tag = tag, throwable = throwable, data = data, message = message)
    }

    fun w(
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        DPLog.w(tag = tag, throwable = throwable, data = data, message = message)
    }

    fun e(
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        DPLog.e(tag = tag, throwable = throwable, data = data, message = message)
    }

    fun a(
        throwable: Throwable? = null,
        data: Map<String, String> = emptyMap(),
        message: () -> String,
    ) {
        DPLog.a(tag = tag, throwable = throwable, data = data, message = message)
    }
}
