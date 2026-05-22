package dev.shounakmulay.devpulse.core.logging

import org.koin.core.annotation.Factory

@Factory
class DPLogger internal constructor(
    private var tag: String = "DevPulse",
) {
    fun withTag(tag: String): DPLogger {
        this.tag = tag
        return this
    }

    fun v(throwable: Throwable? = null, message: () -> String) {
        DPLog.v(tag = tag, throwable = throwable, message = message)
    }

    fun d(throwable: Throwable? = null, message: () -> String) {
        DPLog.d(tag = tag, throwable = throwable, message = message)
    }

    fun i(throwable: Throwable? = null, message: () -> String) {
        DPLog.i(tag = tag, throwable = throwable, message = message)
    }

    fun w(throwable: Throwable? = null, message: () -> String) {
        DPLog.w(tag = tag, throwable = throwable, message = message)
    }

    fun e(throwable: Throwable? = null, message: () -> String) {
        DPLog.e(tag = tag, throwable = throwable, message = message)
    }

    fun a(throwable: Throwable? = null, message: () -> String) {
        DPLog.a(tag = tag, throwable = throwable, message = message)
    }
}
