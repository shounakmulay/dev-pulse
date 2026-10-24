package dev.shounakmulay.devpulse.core.logging.log

inline fun <reified T : Any> logger(): DPLogger = DPLog.tag(T::class.simpleName ?: "Unknown")

fun Any.logger(): DPLogger = DPLog.tag(this::class.simpleName ?: UNKNOWN_TAG)
